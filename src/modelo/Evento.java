package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Evento de la agenda de la visita del Papa. Ademas del horario de inicio
 * (horario) guarda un horario de fin estimado (horarioFin), necesario para
 * detectar superposiciones entre eventos.
 */
public class Evento {

    // Datos del evento y horario de fin estimado (esto ultimo no esta en el UML).
    private int id;
    private LocalDate fecha;
    private LocalTime horario;
    private LocalTime horarioFin;
    private String actividad;
    private Ciudad ciudad;

    // Armo el evento y dejo que los setters validen los datos.
    public Evento(int id, LocalDate fecha, LocalTime horario, LocalTime horarioFin,
                  String actividad, Ciudad ciudad) {
        this.id = id;
        setFecha(fecha);
        setHorario(horario);
        setHorarioFin(horarioFin);
        setActividad(actividad);
        setCiudad(ciudad);
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

    /** Horario de inicio del evento. */
    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario de inicio es obligatorio.");
        }
        this.horario = horario;
    }

    /** Horario de fin estimado del evento. */
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

    /**
     * Determina si este evento se superpone con otro. Solo pueden superponerse
     * eventos del mismo dia; el fin es exclusivo, por lo que un evento que
     * termina a las 12:00 no se superpone con otro que empieza a las 12:00.
     */
    public boolean seSuperpone(Evento otro) {
        // Para pisarse tienen que ser el mismo dia y los rangos tienen que
        // cruzarse. Tomo el fin como exclusivo, asi 10-12 y 12-14 no se pisan.
        return this.fecha.equals(otro.fecha)
                && this.horario.isBefore(otro.horarioFin)
                && otro.horario.isBefore(this.horarioFin);
    }
}
