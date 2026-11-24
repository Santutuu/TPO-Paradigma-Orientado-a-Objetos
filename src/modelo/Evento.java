package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Evento de la agenda de la visita del Papa. Ademas del horario de inicio
 * (horario) guarda un horario de fin estimado (horarioFin), necesario para
 * detectar superposiciones entre eventos. El estado permite cancelarlo sin
 * borrar sus datos, para conservar el historial. La capacidad maxima limita la
 * cantidad de asistentes; el valor 0 significa sin limite.
 */
public class Evento {

    private int id;
    private LocalDate fecha;
    private LocalTime horario;
    private LocalTime horarioFin;
    private String actividad;
    private Ciudad ciudad;
    private String direccion;
    private int capacidadMaxima;
    private EstadoEvento estado;

    public Evento(int id, LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                  String actividad, Ciudad ciudad, String direccion, int capacidadMaxima,
                  EstadoEvento estado) {
        this.id = id;
        setFecha(fecha);
        setHorario(horario);
        setHorarioFin(horarioFin);
        setActividad(actividad);
        setCiudad(ciudad);
        setDireccion(direccion);
        setCapacidadMaxima(capacidadMaxima);
        setEstado(estado);
    }

    /** Constructor compatible: crea el evento en estado ACTIVA. */
    public Evento(int id, LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                  String actividad, Ciudad ciudad, String direccion, int capacidadMaxima) {
        this(id, fecha, horario, horarioFin, actividad, ciudad, direccion, capacidadMaxima,
                EstadoEvento.ACTIVA);
    }

    /** Constructor compatible: sin capacidad definida (sin limite) y ACTIVA. */
    public Evento(int id, LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                  String actividad, Ciudad ciudad, String direccion) {
        this(id, fecha, horario, horarioFin, actividad, ciudad, direccion, 0, EstadoEvento.ACTIVA);
    }

    /** Constructor compatible: sin capacidad definida (sin limite). */
    public Evento(int id, LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                  String actividad, Ciudad ciudad, String direccion, EstadoEvento estado) {
        this(id, fecha, horario, horarioFin, actividad, ciudad, direccion, 0, estado);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        this.fecha = fecha;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario de inicio es obligatorio.");
        }
        this.horario = horario;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    /**
     * El horario de fin debe ser posterior al de inicio (los eventos no cruzan
     * la medianoche).
     */
    public void setHorarioFin(LocalTime horarioFin) {
        if (horarioFin == null) {
            throw new IllegalArgumentException("El horario de fin es obligatorio.");
        }
        if (horario != null && !horarioFin.isAfter(horario)) {
            throw new IllegalArgumentException("El horario de fin debe ser posterior al de inicio.");
        }
        this.horarioFin = horarioFin;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        if (actividad == null || actividad.trim().isEmpty()) {
            throw new IllegalArgumentException("La actividad no puede estar vacia.");
        }
        this.actividad = actividad.trim();
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad es obligatoria.");
        }
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion del evento es obligatoria.");
        }
        this.direccion = direccion.trim();
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Capacidad maxima de asistentes. El valor 0 significa que el evento no
     * tiene limite de capacidad.
     */
    public void setCapacidadMaxima(int capacidadMaxima) {
        if (capacidadMaxima < 0) {
            throw new IllegalArgumentException("La capacidad del evento no puede ser negativa.");
        }
        this.capacidadMaxima = capacidadMaxima;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del evento es obligatorio.");
        }
        this.estado = estado;
    }

    /** Cancela el evento conservando sus datos como historial. */
    public void cancelar() {
        this.estado = EstadoEvento.CANCELADA;
    }

    public boolean estaActiva() {
        return this.estado == EstadoEvento.ACTIVA;
    }

    public boolean seSuperpone(Evento otro) {
        return this.fecha.equals(otro.fecha)
                && this.horario.isBefore(otro.horarioFin)
                && otro.horario.isBefore(this.horarioFin);
    }
}
