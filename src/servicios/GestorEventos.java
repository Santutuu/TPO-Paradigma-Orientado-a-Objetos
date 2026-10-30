package servicios;

import modelo.*;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona el alta, la modificacion y la consulta de eventos, asi como la
 * inscripcion y desinscripcion de asistentes.
 */
public class GestorEventos {

    // Lista de eventos cargada desde eventos.json.
    private final List<Evento> eventos;
    // Lo necesito para saber que asistentes estan inscriptos.
    private final GestorUsuarios gestorUsuarios;
    private final PersistenciaJSON persistencia;

    public GestorEventos(List<Evento> eventos, GestorUsuarios gestorUsuarios,
                         PersistenciaJSON persistencia) {
        this.eventos = eventos;
        this.gestorUsuarios = gestorUsuarios;
        this.persistencia = persistencia;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public boolean existeId(int id) {
        return buscarPorId(id) != null;
    }

    public Evento buscarPorId(int id) {
        for (Evento e : eventos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /**
     * Registra un evento nuevo con un id autogenerado y guarda en disco.
     */
    public Evento registrar(LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                            String actividad, Ciudad ciudad) {
        // Creo el evento con el id autogenerado y lo guardo en el archivo.
        Evento evento = new Evento(proximoId(), fecha, horario, horarioFin, actividad, ciudad);
        eventos.add(evento);
        guardarEventos();
        return evento;
    }

    /**
     * Modifica un evento existente conservando su identificador. No permite que
     * el nuevo horario se superponga con otro evento al que ya esten inscriptos
     * sus asistentes.
     */
    public void modificarEvento(int id, LocalDate fecha, LocalTime horario,
                                LocalTime horarioFin, String actividad, Ciudad ciudad) {
        Evento evento = buscarPorId(id);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + id + ".");
        }

        // Armo el evento con los datos nuevos y reviso que no choque con otro
        // evento de algun asistente ya inscripto.
        Evento propuesto = new Evento(id, fecha, horario, horarioFin, actividad, ciudad);
        for (Usuario u : gestorUsuarios.getUsuarios()) {
            if (u instanceof Asistente && ((Asistente) u).estaInscrito(id)) {
                Asistente asistente = (Asistente) u;
                // Si el cambio de horario pisa otro evento del asistente, no lo dejo.
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
        guardarEventos();
        // Las cuentas guardan los ids de los eventos, pero se reescriben por si
        // el evento cambio y para mantener ambos archivos consistentes.
        guardarCuentas();
    }

    public List<Evento> listarTodosOrdenados() {
        List<Evento> copia = new ArrayList<>(eventos);
        copia.sort(Comparator.comparing(Evento::getFecha).thenComparing(Evento::getHorario));
        return copia;
    }

    /**
     * Devuelve los eventos de una fecha, ordenados por horario de inicio.
     */
    public List<Evento> agendaPorFecha(LocalDate fecha) {
        List<Evento> resultado = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getFecha().equals(fecha)) {
                resultado.add(e);
            }
        }
        resultado.sort(Comparator.comparing(Evento::getHorario));
        return resultado;
    }

    /**
     * Inscribe a un asistente en un evento existente. No permite inscribirse
     * dos veces al mismo evento ni en dos eventos que se superpongan en el
     * tiempo (misma fecha y horarios que se pisan), sin importar la ciudad.
     */
    public void inscribir(Asistente asistente, int idEvento) {
        Evento evento = buscarPorId(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + idEvento + ".");
        }
        if (asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("Ya estas inscrito en ese evento.");
        }
        // Me fijo si el evento nuevo se pisa con alguno de los que ya tiene.
        Evento choque = buscarSuperpuesto(asistente, evento, evento);
        if (choque != null) {
            throw new IllegalArgumentException("No podes inscribirte: se superpone con \""
                    + choque.getActividad() + "\" (" + choque.getHorario()
                    + " a " + choque.getHorarioFin() + "), al que ya estas inscripto.");
        }
        asistente.inscribir(idEvento);
        guardarCuentas();
    }

    /**
     * Desinscribe a un asistente de un evento.
     */
    public void desinscribir(Asistente asistente, int idEvento) {
        // Si no estaba inscripto, aviso en vez de borrar cualquier cosa.
        if (!asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("No estas inscrito en ese evento.");
        }
        asistente.desinscribir(idEvento);
        guardarCuentas();
    }

    /**
     * Devuelve el evento del asistente que se superpone con el candidato, o
     * null si no hay ninguno.
     */
    private Evento buscarSuperpuesto(Asistente asistente, Evento candidato, Evento excluido) {
        // Recorro los ids de los eventos del asistente y comparo horarios.
        for (int idInscripto : asistente.getEventos()) {
            Evento inscripto = buscarPorId(idInscripto);
            if (inscripto != null && inscripto != excluido && inscripto.seSuperpone(candidato)) {
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

    private void guardarEventos() {
        try {
            persistencia.guardarEventos(eventos);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar eventos.json: " + e.getMessage(), e);
        }
    }

    private void guardarCuentas() {
        try {
            persistencia.guardarCuentas(gestorUsuarios.getUsuarios());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
