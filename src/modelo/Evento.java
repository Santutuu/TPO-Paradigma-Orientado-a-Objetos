package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Evento de la agenda de la visita del Papa.
 */
public class Evento {

    private int id;
    private LocalDate fecha;
    private LocalTime horario;
    private String actividad;
    private Ciudad ciudad;

    public Evento(int id, LocalDate fecha, LocalTime horario, String actividad, Ciudad ciudad) {
        this.id = id;
        setFecha(fecha);
        setHorario(horario);
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

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario es obligatorio.");
        }
        this.horario = horario;
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
}
