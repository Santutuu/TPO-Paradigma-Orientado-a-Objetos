package servicios;

import modelo.*;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona la creacion, consulta y cancelacion de reservas, y calcula la
 * disponibilidad de los alojamientos.
 */
public class GestorReservas {

    private final List<Reserva> reservas;
    private final GestorUsuarios gestorUsuarios;
    private final PersistenciaJSON persistencia;

    public GestorReservas(List<Reserva> reservas, GestorUsuarios gestorUsuarios,
                          PersistenciaJSON persistencia) {
        this.reservas = reservas;
        this.gestorUsuarios = gestorUsuarios;
        this.persistencia = persistencia;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public Reserva buscarPorId(int id) {
        for (Reserva r : reservas) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    /**
     * Devuelve la reserva activa de un asistente, o null si no tiene.
     */
    public Reserva reservaActivaDe(int idAsistente) {
        for (Reserva r : reservas) {
            if (r.getIdAsistente() == idAsistente && r.estaActiva()) {
                return r;
            }
        }
        return null;
    }

    /**
     * Crea una reserva nueva validando todas las reglas de negocio.
     */
    public Reserva crearReserva(int idAsistente, int idAlojamiento,
                                LocalDate ingreso, LocalDate egreso) {
        if (gestorUsuarios.buscarAsistente(idAsistente) == null) {
            throw new IllegalArgumentException("El asistente indicado no existe.");
        }
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento indicado no existe.");
        }
        if (ingreso == null || egreso == null) {
            throw new IllegalArgumentException("Las fechas de ingreso y egreso son obligatorias.");
        }
        if (!egreso.isAfter(ingreso)) {
            throw new IllegalArgumentException("El egreso debe ser posterior al ingreso.");
        }
        if (reservaActivaDe(idAsistente) != null) {
            throw new IllegalArgumentException("Ya tenes una reserva activa. Cancelala antes de reservar otro alojamiento.");
        }

        int ocupacionMaxima = ocupacionMaxima(idAlojamiento, ingreso, egreso);
        if (ocupacionMaxima + 1 > alojamiento.getCapacidad()) {
            throw new IllegalArgumentException("No hay capacidad disponible para todo el periodo solicitado. "
                    + "Ocupacion maxima actual: " + ocupacionMaxima + " de " + alojamiento.getCapacidad() + ".");
        }

        Reserva reserva = new Reserva(proximoId(), idAsistente, idAlojamiento,
                ingreso, egreso, EstadoReserva.ACTIVA);
        reservas.add(reserva);
        guardarReservas();
        return reserva;
    }

    /**
     * Cancela una reserva activa y persiste el cambio.
     */
    public void cancelarReserva(int idReserva) {
        Reserva reserva = buscarPorId(idReserva);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con id " + idReserva + ".");
        }
        if (!reserva.estaActiva()) {
            throw new IllegalArgumentException("La reserva ya esta cancelada.");
        }
        reserva.cancelar();
        guardarReservas();
    }

    public List<Reserva> reservasDeAlojamiento(int idAlojamiento) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getIdAlojamiento() == idAlojamiento) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Reserva::getIngreso));
        return resultado;
    }

    /**
     * Cantidad de reservas activas de un alojamiento que se superponen con el
     * periodo solicitado, calculada dia por dia para respetar la capacidad en
     * cada tramo (no solo en la fecha de ingreso).
     */
    public int ocupacionMaxima(int idAlojamiento, LocalDate ingreso, LocalDate egreso) {
        int maximo = 0;
        for (LocalDate dia = ingreso; dia.isBefore(egreso); dia = dia.plusDays(1)) {
            int ocupados = 0;
            for (Reserva r : reservas) {
                if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()
                        && !r.getIngreso().isAfter(dia) && r.getEgreso().isAfter(dia)) {
                    ocupados++;
                }
            }
            if (ocupados > maximo) {
                maximo = ocupados;
            }
        }
        return maximo;
    }

    /**
     * Ocupacion maxima considerando todas las reservas activas del alojamiento
     * (se usa para validar la modificacion de capacidad).
     */
    public int ocupacionMaxima(int idAlojamiento) {
        LocalDate desde = null;
        LocalDate hasta = null;
        for (Reserva r : reservas) {
            if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()) {
                if (desde == null || r.getIngreso().isBefore(desde)) {
                    desde = r.getIngreso();
                }
                if (hasta == null || r.getEgreso().isAfter(hasta)) {
                    hasta = r.getEgreso();
                }
            }
        }
        if (desde == null) {
            return 0;
        }
        return ocupacionMaxima(idAlojamiento, desde, hasta);
    }

    /**
     * Plazas disponibles del alojamiento durante todo el periodo solicitado.
     */
    public int disponibilidad(int idAlojamiento, LocalDate ingreso, LocalDate egreso) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        return alojamiento.getCapacidad() - ocupacionMaxima(idAlojamiento, ingreso, egreso);
    }

    private int proximoId() {
        int maximo = 0;
        for (Reserva r : reservas) {
            if (r.getId() > maximo) {
                maximo = r.getId();
            }
        }
        return maximo + 1;
    }

    private void guardarReservas() {
        try {
            persistencia.guardarReservas(reservas);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar reservas.json: " + e.getMessage(), e);
        }
    }
}
