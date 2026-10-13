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

    private final List<Evento> eventos;
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
     * Registra un evento nuevo. Valida que el id no se repita y guarda en disco.
     */
    public Evento registrarEvento(Evento evento) {
        if (existeId(evento.getId())) {
            throw new IllegalArgumentException("Ya existe un evento con ese identificador.");
        }
        eventos.add(evento);
        guardarEventos();
        return evento;
    }

    /**
     * Modifica un evento conservando su identificador.
     */
    public void modificarEvento(int id, LocalDate fecha, LocalTime horario,
                                String actividad, Ciudad ciudad) {
        Evento evento = buscarPorId(id);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con id " + id + ".");
        }
        if (actividad == null || actividad.trim().isEmpty()) {
            throw new IllegalArgumentException("La actividad no puede estar vacia.");
        }
        evento.setFecha(fecha);
        evento.setHorario(horario);
        evento.setActividad(actividad);
        evento.setCiudad(ciudad);
        guardarEventos();
    }

    public List<Evento> listarTodosOrdenados() {
        List<Evento> copia = new ArrayList<>(eventos);
        copia.sort(Comparator.comparing(Evento::getFecha).thenComparing(Evento::getHorario));
        return copia;
    }

    public List<Evento> listarPorFecha(LocalDate fecha) {
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
     * Inscribe a un asistente en un evento existente.
     */
    public void inscribir(Asistente asistente, int idEvento) {
        if (!existeId(idEvento)) {
            throw new IllegalArgumentException("No existe un evento con id " + idEvento + ".");
        }
        if (asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("Ya estas inscrito en ese evento.");
        }
        asistente.inscribir(idEvento);
        guardarCuentas();
    }

    /**
     * Desinscribe a un asistente de un evento.
     */
    public void desinscribir(Asistente asistente, int idEvento) {
        if (!asistente.estaInscrito(idEvento)) {
            throw new IllegalArgumentException("No estas inscrito en ese evento.");
        }
        asistente.desinscribir(idEvento);
        guardarCuentas();
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
