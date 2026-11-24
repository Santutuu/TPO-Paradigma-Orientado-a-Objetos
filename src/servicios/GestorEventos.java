package servicios;

import modelo.*;
import persistencia.ErrorBaseDatos;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestorEventos {

    private final List<Evento> eventos;
    private final GestorUsuarios gestorUsuarios;
    private final PersistenciaJSON persistencia;
    private final List<EventoObserver> observadores;

    public GestorEventos(List<Evento> eventos, GestorUsuarios gestorUsuarios,
                         PersistenciaJSON persistencia) {
        this.eventos = eventos;
        this.gestorUsuarios = gestorUsuarios;
        this.persistencia = persistencia;
        this.observadores = new ArrayList<>();
    }

    /** Agrega un observador que sera notificado ante cambios de eventos. */
    public synchronized void agregarObservador(EventoObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public synchronized List<Evento> getEventos() {
        return new ArrayList<>(eventos);
    }

    public synchronized boolean existeId(int id) {
        return buscarPorId(id) != null;
    }

    public synchronized Evento buscarPorId(int id) {
        for (Evento e : eventos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /** Cantidad de asistentes inscriptos en el evento. */
    public synchronized int contarInscriptos(int idEvento) {
        int total = 0;
        for (Usuario u : gestorUsuarios.getUsuarios()) {
            if (u.esAsistente() && ((Asistente) u).estaInscrito(idEvento)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Registra un evento nuevo con un id autogenerado y guarda en disco.
     */
    public synchronized Evento registrar(LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                                         String actividad, Ciudad ciudad, String direccion) {
        return registrar(fecha, horario, horarioFin, actividad, ciudad, direccion, 0);
    }

    /** Registra un evento con capacidad maxima; 0 significa sin limite. */
    public synchronized Evento registrar(LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                                         String actividad, Ciudad ciudad, String direccion,
                                         int capacidadMaxima) {
        Evento evento = new Evento(proximoId(), fecha, horario, horarioFin, actividad, ciudad,
                direccion, capacidadMaxima);
        eventos.add(evento);
        guardarEventos();
        notificarEventoCreado(evento);
        return evento;
    }

    public synchronized void modificarEvento(int id, LocalDate fecha, LocalTime horario,
                                             LocalTime horarioFin, String actividad, Ciudad ciudad,
                                             String direccion) {
        Evento evento = buscarPorId(id);
        int capacidadMaxima = evento == null ? 0 : evento.getCapacidadMaxima();
        modificarEvento(id, fecha, horario, horarioFin, actividad, ciudad, direccion,
                capacidadMaxima);
    }

    public synchronized void modificarEvento(int id, LocalDate fecha, LocalTime horario,
                                             LocalTime horarioFin, String actividad, Ciudad ciudad,
                                             String direccion, int capacidadMaxima) {
        Evento evento = buscarPorId(id);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + id + ".");
        }
        if (!evento.estaActiva()) {
            throw new IllegalArgumentException("No se puede modificar un evento cancelado.");
        }
        int inscriptos = contarInscriptos(id);
        if (capacidadMaxima > 0 && capacidadMaxima < inscriptos) {
            throw new IllegalArgumentException("No se puede reducir la capacidad a " + capacidadMaxima
                    + " porque hay " + inscriptos + " asistentes inscriptos.");
        }

        Evento propuesto = new Evento(id, fecha, horario, horarioFin, actividad, ciudad, direccion,
                capacidadMaxima);
        for (Usuario u : gestorUsuarios.getUsuarios()) {
            if (u.esAsistente() && ((Asistente) u).estaInscrito(id)) {
                Asistente asistente = (Asistente) u;
                Evento choque = buscarSuperpuesto(asistente, propuesto, evento);
                if (choque != null) {
                    throw new IllegalArgumentException("No se puede modificar: " + asistente.getNombre()
                            + " esta inscripto en \"" + evento.getActividad()
                            + "\" y el nuevo horario se superpone con \"" + choque.getActividad()
                            + "\" (" + choque.getHorario() + " a " + choque.getHorarioFin() + ").");
                }
            }
        }

        evento.setFecha(fecha);
        evento.setHorario(horario);
        evento.setHorarioFin(horarioFin);
        evento.setActividad(actividad);
        evento.setCiudad(ciudad);
        evento.setDireccion(direccion);
        evento.setCapacidadMaxima(capacidadMaxima);
        guardarEventos();

        guardarCuentas();
        notificarEventoModificado(evento);
    }

    public synchronized void cancelarEvento(int id) {
        Evento evento = buscarPorId(id);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + id + ".");
        }
        if (!evento.estaActiva()) {
            throw new IllegalArgumentException("El evento ya esta cancelado.");
        }
        evento.cancelar();
        guardarEventos();
        notificarEventoCancelado(evento);
    }

    public synchronized List<Evento> listarTodosOrdenados() {
        List<Evento> copia = new ArrayList<>(eventos);
        copia.sort(Comparator.comparing(Evento::getFecha).thenComparing(Evento::getHorario));
        return copia;
    }

    public synchronized List<Evento> agendaPorFecha(LocalDate fecha) {
        List<Evento> resultado = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getFecha().equals(fecha)) {
                resultado.add(e);
            }
        }
        resultado.sort(Comparator.comparing(Evento::getHorario));
        return resultado;
    }

    public synchronized void inscribir(Asistente asistente, int idEvento) {
        Evento evento = buscarPorId(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + idEvento + ".");
        }
        if (!evento.estaActiva()) {
            throw new IllegalArgumentException("El evento esta cancelado.");
        }
        if (asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("Ya estas inscrito en ese evento.");
        }
        Evento choque = buscarSuperpuesto(asistente, evento, evento);
        if (choque != null) {
            throw new IllegalArgumentException("No podes inscribirte: se superpone con \""
                    + choque.getActividad() + "\" (" + choque.getHorario()
                    + " a " + choque.getHorarioFin() + "), al que ya estas inscripto.");
        }
        if (evento.getCapacidadMaxima() > 0
                && contarInscriptos(idEvento) >= evento.getCapacidadMaxima()) {
            throw new IllegalArgumentException("No se puede registrar al evento porque alcanzo "
                    + "la capacidad maxima de " + evento.getCapacidadMaxima()
                    + " asistentes.");
        }
        asistente.inscribir(idEvento);
        guardarCuentas();
    }

    public synchronized void desinscribir(Asistente asistente, int idEvento) {
        if (!asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("No estas inscrito en ese evento.");
        }
        asistente.desinscribir(idEvento);
        guardarCuentas();
    }

    private Evento buscarSuperpuesto(Asistente asistente, Evento candidato, Evento excluido) {
        for (int idInscripto : asistente.getEventos()) {
            Evento inscripto = buscarPorId(idInscripto);
            if (inscripto != null && inscripto != excluido && inscripto.estaActiva()
                    && inscripto.seSuperpone(candidato)) {
                return inscripto;
            }
        }
        return null;
    }

    private int proximoId() {
        int maximo = 0;
        for (Evento e : eventos) {
            if (e.getId() > maximo) {
                maximo = e.getId();
            }
        }
        return maximo + 1;
    }

    private void notificarEventoCreado(Evento evento) {
        for (EventoObserver observador : new ArrayList<>(observadores)) {
            try {
                observador.eventoCreado(evento);
            } catch (RuntimeException ex) {
                System.err.println("Un observador fallo al notificar un evento creado: "
                        + ex.getMessage());
            }
        }
    }

    private void notificarEventoModificado(Evento evento) {
        for (EventoObserver observador : new ArrayList<>(observadores)) {
            try {
                observador.eventoModificado(evento);
            } catch (RuntimeException ex) {
                System.err.println("Un observador fallo al notificar un evento modificado: "
                        + ex.getMessage());
            }
        }
    }

    private void notificarEventoCancelado(Evento evento) {
        for (EventoObserver observador : new ArrayList<>(observadores)) {
            try {
                observador.eventoCancelado(evento);
            } catch (RuntimeException ex) {
                System.err.println("Un observador fallo al notificar un evento cancelado: "
                        + ex.getMessage());
            }
        }
    }

    private void guardarEventos() {
        try {
            persistencia.guardarEventos(eventos);
        } catch (IOException e) {
            throw new ErrorBaseDatos("No se pudo guardar eventos.json: " + e.getMessage(), e);
        }
    }

    private void guardarCuentas() {
        try {
            persistencia.guardarCuentas(gestorUsuarios.getUsuarios());
        } catch (IOException e) {
            throw new ErrorBaseDatos("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
