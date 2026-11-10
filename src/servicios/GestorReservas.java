package servicios;

import modelo.*;
import persistencia.ErrorBaseDatos;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona la creacion, consulta y cancelacion de reservas, y calcula la
 * disponibilidad de los alojamientos.
 *
 * En hoteles cada reserva ocupa una habitacion completa de un tipo (1 a 4
 * personas); en parroquias y espacios de organizacion cada reserva representa
 * una persona y se controla la capacidad total.
 */
public class GestorReservas {

    private final List<Reserva> reservas;
    private final GestorUsuarios gestorUsuarios;
    private final PersistenciaJSON persistencia;
    private final List<ReservaObserver> observadores;

    public GestorReservas(List<Reserva> reservas, GestorUsuarios gestorUsuarios,
                          PersistenciaJSON persistencia) {
        this.reservas = reservas;
        this.gestorUsuarios = gestorUsuarios;
        this.persistencia = persistencia;
        this.observadores = new ArrayList<>();
        completarReservasDeHotelSinTipo();
    }

    public void agregarObservador(ReservaObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    private void completarReservasDeHotelSinTipo() {
        for (Reserva r : reservas) {
            if (r.getTipoHabitacion() != 0) {
                continue;
            }
            Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(r.getIdAlojamiento());
            if (!alojamiento.esHotel()) {
                continue;
            }
            Hotel hotel = (Hotel) alojamiento;
            for (int tipo = 1; tipo <= Hotel.MAX_PERSONAS_POR_HABITACION; tipo++) {
                if (hotel.getCantidadHabitaciones(tipo) > 0) {
                    r.setTipoHabitacion(tipo);
                    if (r.getPrecioPorNoche() == 0) {
                        r.setPrecioPorNoche(hotel.getPrecioPorNoche(tipo));
                    }
                    break;
                }
            }
        }
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

    public List<Reserva> reservasActivasDeAsistente(int idAsistente) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getIdAsistente() == idAsistente && r.estaActiva()) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Reserva::getIngreso));
        return resultado;
    }

    public List<Reserva> reservasDeAsistente(int idAsistente) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getIdAsistente() == idAsistente) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Reserva::getIngreso).reversed()
                .thenComparing(Comparator.comparing(Reserva::getId).reversed()));
        return resultado;
    }

    public Reserva crearReserva(int idAsistente, int idAlojamiento,
                                LocalDate ingreso, LocalDate egreso) {
        return crearReserva(idAsistente, idAlojamiento, 0, 1, ingreso, egreso);
    }

    public Reserva crearReserva(int idAsistente, int idAlojamiento,
                                int tipoHabitacion, int cantidadPersonas,
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
        for (Reserva existente : reservas) {
            if (existente.getIdAsistente() == idAsistente && existente.estaActiva()
                    && existente.getIngreso().isBefore(egreso)
                    && ingreso.isBefore(existente.getEgreso())) {
                throw new IllegalArgumentException("Ya tenes una reserva activa del "
                        + existente.getIngreso() + " al " + existente.getEgreso()
                        + " que se superpone con ese periodo.");
            }
        }

        int tipo;
        int personas;
        double precioPorNoche;
        if (alojamiento.esHotel()) {
            Hotel hotel = (Hotel) alojamiento;
            if (tipoHabitacion < 1 || tipoHabitacion > Hotel.MAX_PERSONAS_POR_HABITACION) {
                throw new IllegalArgumentException("Elegi un tipo de habitacion valido (1 a 4 personas).");
            }
            if (hotel.getCantidadHabitaciones(tipoHabitacion) == 0) {
                throw new IllegalArgumentException("El hotel no tiene habitaciones para "
                        + descripcionTipo(tipoHabitacion) + ".");
            }
            if (cantidadPersonas < 1 || cantidadPersonas > tipoHabitacion) {
                throw new IllegalArgumentException("La cantidad de personas debe estar entre 1 y "
                        + tipoHabitacion + " para ese tipo de habitacion.");
            }
            if (habitacionesDisponibles(idAlojamiento, tipoHabitacion, ingreso, egreso) <= 0) {
                throw new IllegalArgumentException("No hay habitaciones disponibles para "
                        + tipoHabitacion + " personas en todo el periodo solicitado.");
            }
            precioPorNoche = hotel.getPrecioPorNoche(tipoHabitacion);
            if (precioPorNoche <= 0) {
                throw new IllegalArgumentException("El hotel no tiene configurado el precio para "
                        + descripcionTipo(tipoHabitacion) + ".");
            }
            tipo = tipoHabitacion;
            personas = cantidadPersonas;
        } else {
            if (tipoHabitacion != 0 || cantidadPersonas != 1) {
                throw new IllegalArgumentException("Solo los hoteles se reservan por tipo de habitacion.");
            }
            int ocupacionMaxima = ocupacionMaxima(idAlojamiento, ingreso, egreso);
            if (ocupacionMaxima + 1 > alojamiento.getCapacidad()) {
                throw new IllegalArgumentException("No hay capacidad disponible para todo el periodo solicitado. "
                        + "Ocupacion maxima actual: " + ocupacionMaxima + " de " + alojamiento.getCapacidad() + ".");
            }
            tipo = 0;
            personas = 1;
            precioPorNoche = 0.0;
        }

        Reserva reserva = new Reserva(proximoId(), idAsistente, idAlojamiento,
                tipo, personas, precioPorNoche, ingreso, egreso, EstadoReserva.ACTIVA);
        reservas.add(reserva);
        guardarReservas();
        notificarReservaCreada(reserva);
        return reserva;
    }

    /**
     * Cancela una reserva activa y persiste el cambio. Solo se permite hasta
     * 24 horas antes del ingreso; despues de ese limite la reserva ya no puede
     * cancelarse.
     */
    public void cancelarReserva(int idReserva) {
        cancelarValidando(buscarReservaOError(idReserva));
    }

    /**
     * Cancela una reserva propia del asistente autenticado. Ademas del limite
     * de 24 horas, valida que la reserva pertenezca a ese asistente.
     */
    public void cancelarReservaDeAsistente(int idSesion, int idReserva) {
        Reserva reserva = buscarReservaOError(idReserva);
        if (reserva.getIdAsistente() != idSesion) {
            throw new IllegalArgumentException("Solo podes cancelar tus propias reservas.");
        }
        cancelarValidando(reserva);
    }

    /**
     * Cancela una reserva recibida por el alojamiento autenticado. Ademas del
     * limite de 24 horas, valida que la reserva sea de ese alojamiento.
     */
    public void cancelarReservaDeAlojamiento(int idSesion, int idReserva) {
        Reserva reserva = buscarReservaOError(idReserva);
        if (reserva.getIdAlojamiento() != idSesion) {
            throw new IllegalArgumentException("Solo podes cancelar reservas de tu alojamiento.");
        }
        cancelarValidando(reserva);
    }

    private Reserva buscarReservaOError(int idReserva) {
        Reserva reserva = buscarPorId(idReserva);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con id " + idReserva + ".");
        }
        return reserva;
    }

    private void cancelarValidando(Reserva reserva) {
        if (!reserva.estaActiva()) {
            throw new IllegalArgumentException("La reserva ya esta cancelada.");
        }

        LocalDateTime limite = reserva.getIngreso().atStartOfDay().minusHours(24);
        if (LocalDateTime.now().isAfter(limite)) {
            throw new IllegalArgumentException("Solo podes cancelar con al menos 24 horas de anticipacion "
                    + "al ingreso (limite: " + limite.toLocalDate() + " " + limite.toLocalTime() + ").");
        }

        reserva.cancelar();
        guardarReservas();
        notificarReservaCancelada(reserva);
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

    public List<Reserva> reservasActivasDeAlojamiento(int idAlojamiento) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Reserva::getIngreso));
        return resultado;
    }

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
     * (se usa para validar la modificacion de capacidad de alojamientos que no
     * son hoteles).
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

    public int ocupacionMaximaHabitaciones(int idAlojamiento, int tipoHabitacion) {
        LocalDate desde = null;
        LocalDate hasta = null;
        for (Reserva r : reservas) {
            if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()
                    && r.getTipoHabitacion() == tipoHabitacion) {
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

        int maximo = 0;
        for (LocalDate dia = desde; dia.isBefore(hasta); dia = dia.plusDays(1)) {
            int ocupadas = 0;
            for (Reserva r : reservas) {
                if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()
                        && r.getTipoHabitacion() == tipoHabitacion
                        && !r.getIngreso().isAfter(dia) && r.getEgreso().isAfter(dia)) {
                    ocupadas++;
                }
            }
            if (ocupadas > maximo) {
                maximo = ocupadas;
            }
        }
        return maximo;
    }

    /**
     * Habitaciones libres de un tipo para todo el periodo solicitado.
     */
    public int habitacionesDisponibles(int idAlojamiento, int tipoHabitacion,
                                       LocalDate ingreso, LocalDate egreso) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (!alojamiento.esHotel()) {
            throw new IllegalArgumentException("El alojamiento no es un hotel.");
        }
        Hotel hotel = (Hotel) alojamiento;
        int ocupadasMaximas = 0;
        for (LocalDate dia = ingreso; dia.isBefore(egreso); dia = dia.plusDays(1)) {
            int ocupadas = 0;
            for (Reserva r : reservas) {
                if (r.getIdAlojamiento() == idAlojamiento && r.estaActiva()
                        && r.getTipoHabitacion() == tipoHabitacion
                        && !r.getIngreso().isAfter(dia) && r.getEgreso().isAfter(dia)) {
                    ocupadas++;
                }
            }
            if (ocupadas > ocupadasMaximas) {
                ocupadasMaximas = ocupadas;
            }
        }
        return hotel.getCantidadHabitaciones(tipoHabitacion) - ocupadasMaximas;
    }

    public int disponibilidad(int idAlojamiento, LocalDate ingreso, LocalDate egreso) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento.esHotel()) {
            int total = 0;
            for (int tipo = 1; tipo <= Hotel.MAX_PERSONAS_POR_HABITACION; tipo++) {
                total += Math.max(0, habitacionesDisponibles(idAlojamiento, tipo, ingreso, egreso));
            }
            return total;
        }
        return alojamiento.getCapacidad() - ocupacionMaxima(idAlojamiento, ingreso, egreso);
    }

    private String descripcionTipo(int tipoHabitacion) {
        return tipoHabitacion == 1 ? "1 persona" : tipoHabitacion + " personas";
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

    private void notificarReservaCreada(Reserva reserva) {
        for (ReservaObserver observador : new ArrayList<>(observadores)) {
            try {
                observador.reservaCreada(reserva);
            } catch (RuntimeException ex) {
                System.err.println("Un observador fallo al notificar una reserva creada: "
                        + ex.getMessage());
            }
        }
    }

    private void notificarReservaCancelada(Reserva reserva) {
        for (ReservaObserver observador : new ArrayList<>(observadores)) {
            try {
                observador.reservaCancelada(reserva);
            } catch (RuntimeException ex) {
                System.err.println("Un observador fallo al notificar una reserva cancelada: "
                        + ex.getMessage());
            }
        }
    }

    private void guardarReservas() {
        try {
            persistencia.guardarReservas(reservas);
        } catch (IOException e) {
            throw new ErrorBaseDatos("No se pudo guardar reservas.json: " + e.getMessage(), e);
        }
    }
}
