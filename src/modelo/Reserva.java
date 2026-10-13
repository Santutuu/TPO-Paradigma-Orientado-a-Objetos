package modelo;

import java.time.LocalDate;

/**
 * Reserva de un alojamiento hecha por un asistente. Guarda identificadores en
 * lugar de los objetos completos para evitar duplicar informacion.
 */
public class Reserva {

    private int id;
    private int idAsistente;
    private int idAlojamiento;
    private LocalDate ingreso;
    private LocalDate egreso;
    private EstadoReserva estado;

    public Reserva(int id, int idAsistente, int idAlojamiento,
                   LocalDate ingreso, LocalDate egreso, EstadoReserva estado) {
        this.id = id;
        this.idAsistente = idAsistente;
        this.idAlojamiento = idAlojamiento;
        setIngreso(ingreso);
        setEgreso(egreso);
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(int idAsistente) {
        this.idAsistente = idAsistente;
    }

    public int getIdAlojamiento() {
        return idAlojamiento;
    }

    public void setIdAlojamiento(int idAlojamiento) {
        this.idAlojamiento = idAlojamiento;
    }

    public LocalDate getIngreso() {
        return ingreso;
    }

    public void setIngreso(LocalDate ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        this.ingreso = ingreso;
    }

    public LocalDate getEgreso() {
        return egreso;
    }

    /**
     * El egreso debe ser posterior al ingreso.
     */
    public void setEgreso(LocalDate egreso) {
        if (egreso == null) {
            throw new IllegalArgumentException("La fecha de egreso es obligatoria.");
        }
        if (!egreso.isAfter(this.ingreso)) {
            throw new IllegalArgumentException("El egreso debe ser posterior al ingreso.");
        }
        this.egreso = egreso;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    /**
     * Determina si dos reservas se superponen en el tiempo. El ingreso es
     * inclusivo y el egreso es exclusivo: si una reserva termina el mismo dia
     * en que empieza la otra, no se consideran superpuestas.
     */
    public boolean seSuperpone(Reserva otra) {
        return this.ingreso.isBefore(otra.egreso) && otra.ingreso.isBefore(this.egreso);
    }

    /**
     * Cancela la reserva.
     */
    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaActiva() {
        return this.estado == EstadoReserva.ACTIVA;
    }
}
