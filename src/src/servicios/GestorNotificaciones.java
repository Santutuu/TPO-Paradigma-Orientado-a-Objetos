package servicios;

import modelo.*;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Guarda y distribuye las notificaciones de eventos y reservas.
 *
 * Implementa los dos observers del sistema: recibe los cambios de eventos y de
 * reservas, decide que usuarios deben enterarse y persiste cada notificacion en
 * notificaciones.json.
 */
public class GestorNotificaciones implements ReservaObserver, EventoObserver {

    private final List<Notificacion> notificaciones;
    private final GestorUsuarios gestorUsuarios;
    private final PersistenciaJSON persistencia;
    private final List<NotificacionListener> listeners;

    public GestorNotificaciones(GestorUsuarios gestorUsuarios, PersistenciaJSON persistencia) {
        this.gestorUsuarios = gestorUsuarios;
        this.persistencia = persistencia;
        this.listeners = new ArrayList<>();
        try {
            this.notificaciones = persistencia.cargarNotificaciones();
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar las notificaciones: "
                    + e.getMessage(), e);
        }
    }

    /** Registra un listener de interfaz para avisar cuando llega una notificacion. */
    public void agregarListener(NotificacionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void quitarListener(NotificacionListener listener) {
        listeners.remove(listener);
    }

    /** Devuelve las notificaciones de un usuario, de la mas nueva a la mas vieja. */
    public List<Notificacion> notificacionesDe(int idDestinatario) {
        List<Notificacion> resultado = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            if (n.getIdDestinatario() == idDestinatario) {
                resultado.add(n);
            }
        }
        resultado.sort(Comparator.comparing(Notificacion::getFechaHora)
                .thenComparingInt(Notificacion::getId).reversed());
        return resultado;
    }

    public int contarNoLeidas(int idDestinatario) {
        int total = 0;
        for (Notificacion n : notificacionesDe(idDestinatario)) {
            if (!n.isLeida()) {
                total++;
            }
        }
        return total;
    }

    /** Marca como leidas todas las notificaciones del usuario y persiste el cambio. */
    public void marcarComoLeidas(int idDestinatario) {
        boolean cambio = false;
        for (Notificacion n : notificaciones) {
            if (n.getIdDestinatario() == idDestinatario && !n.isLeida()) {
                n.marcarLeida();
                cambio = true;
            }
        }
        if (cambio) {
            guardar();
        }
    }

    // ------------------------------------------------------------------ //
    // OBSERVER DE RESERVAS                                                //
    // ------------------------------------------------------------------ //

    @Override
    public void reservaCreada(Reserva reserva) {
        String alojamiento = nombreAlojamiento(reserva.getIdAlojamiento());
        String asistente = nombreUsuario(reserva.getIdAsistente());
        String periodo = "del " + reserva.getIngreso() + " al " + reserva.getEgreso();
        String detalleHabitacion = reserva.getTipoHabitacion() > 0
                ? " (habitacion para " + reserva.getTipoHabitacion() + " personas)" : "";

        List<Notificacion> nuevas = new ArrayList<>();
        nuevas.add(crearNotificacion(reserva.getIdAsistente(), Notificacion.TIPO_RESERVA,
                "Tu reserva #" + reserva.getId() + " en " + alojamiento
                        + " fue confirmada " + periodo + "."));
        nuevas.add(crearNotificacion(reserva.getIdAlojamiento(), Notificacion.TIPO_RESERVA,
                "Nueva reserva #" + reserva.getId() + " de " + asistente + " en "
                        + alojamiento + detalleHabitacion + " " + periodo + "."));
        agregarVarias(nuevas);
    }

    @Override
    public void reservaCancelada(Reserva reserva) {
        String alojamiento = nombreAlojamiento(reserva.getIdAlojamiento());
        String asistente = nombreUsuario(reserva.getIdAsistente());

        List<Notificacion> nuevas = new ArrayList<>();
        nuevas.add(crearNotificacion(reserva.getIdAsistente(), Notificacion.TIPO_RESERVA,
                "Tu reserva #" + reserva.getId() + " en " + alojamiento + " fue cancelada."));
        nuevas.add(crearNotificacion(reserva.getIdAlojamiento(), Notificacion.TIPO_RESERVA,
                "La reserva #" + reserva.getId() + " de " + asistente + " en "
                        + alojamiento + " fue cancelada."));
        agregarVarias(nuevas);
    }

    // ------------------------------------------------------------------ //
    // OBSERVER DE EVENTOS                                                 //
    // ------------------------------------------------------------------ //

    @Override
    public void eventoCreado(Evento evento) {
        String mensaje = "Nuevo evento: " + evento.getActividad()
                + " el " + evento.getFecha() + " de " + evento.getHorario()
                + " a " + evento.getHorarioFin() + " en " + evento.getCiudad()
                + " (" + evento.getDireccion() + ").";

        List<Notificacion> nuevas = new ArrayList<>();
        // El alta de un evento se avisa a todas las cuentas registradas.
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            nuevas.add(crearNotificacion(usuario.getId(), Notificacion.TIPO_EVENTO, mensaje));
        }
        agregarVarias(nuevas);
    }

    @Override
    public void eventoModificado(Evento evento) {
        String mensaje = "Evento modificado: " + evento.getActividad()
                + " ahora es el " + evento.getFecha() + " de " + evento.getHorario()
                + " a " + evento.getHorarioFin() + " en " + evento.getCiudad()
                + " (" + evento.getDireccion() + ").";

        List<Notificacion> nuevas = new ArrayList<>();
        // La modificacion solo se avisa a los asistentes inscriptos en el evento.
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario instanceof Asistente
                    && ((Asistente) usuario).estaInscrito(evento.getId())) {
                nuevas.add(crearNotificacion(usuario.getId(), Notificacion.TIPO_EVENTO, mensaje));
            }
        }
        agregarVarias(nuevas);
    }

    @Override
    public void eventoCancelado(Evento evento) {
        String mensaje = "Evento cancelado: " + evento.getActividad()
                + " del " + evento.getFecha() + " de " + evento.getHorario()
                + " a " + evento.getHorarioFin() + " en " + evento.getCiudad()
                + " (" + evento.getDireccion() + ").";

        List<Notificacion> nuevas = new ArrayList<>();
        // La cancelacion se avisa solo a los asistentes inscriptos en el evento.
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario instanceof Asistente
                    && ((Asistente) usuario).estaInscrito(evento.getId())) {
                nuevas.add(crearNotificacion(usuario.getId(), Notificacion.TIPO_EVENTO, mensaje));
            }
        }
        agregarVarias(nuevas);
    }

    // ------------------------------------------------------------------ //
    // INTERNOS                                                            //
    // ------------------------------------------------------------------ //

    private Notificacion crearNotificacion(int idDestinatario, String tipo, String mensaje) {
        return new Notificacion(0, idDestinatario, LocalDateTime.now(), tipo, mensaje, false);
    }

    private void agregarVarias(List<Notificacion> nuevas) {
        if (nuevas.isEmpty()) {
            return;
        }
        for (Notificacion n : nuevas) {
            n.setId(proximoId());
            notificaciones.add(n);
        }
        guardar();
        for (Notificacion n : nuevas) {
            notificarListener(n);
        }
    }

    private int proximoId() {
        int maximo = 0;
        for (Notificacion n : notificaciones) {
            if (n.getId() > maximo) {
                maximo = n.getId();
            }
        }
        return maximo + 1;
    }

    private void notificarListener(Notificacion notificacion) {
        for (NotificacionListener listener : new ArrayList<>(listeners)) {
            try {
                listener.notificacionAgregada(notificacion);
            } catch (RuntimeException ex) {
                System.err.println("Un listener de notificaciones fallo: " + ex.getMessage());
            }
        }
    }

    private String nombreUsuario(int id) {
        Usuario usuario = gestorUsuarios.buscarPorId(id);
        return usuario == null ? "usuario " + id : usuario.getNombre();
    }

    private String nombreAlojamiento(int id) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(id);
        return alojamiento == null ? "alojamiento " + id : alojamiento.getNombre();
    }

    private void guardar() {
        try {
            persistencia.guardarNotificaciones(notificaciones);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar notificaciones.json: "
                    + e.getMessage(), e);
        }
    }
}
