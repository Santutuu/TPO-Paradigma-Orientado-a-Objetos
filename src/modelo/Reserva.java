package modelo;

import java.time.LocalDate;

public class Reserva {

    private int id;
    private int idAsistente;
    private int idAlojamiento;
    private int tipoHabitacion;
    private int cantidadPersonas;
    private double precioPorNoche;
    private LocalDate ingreso;
    private LocalDate egreso;
    private EstadoReserva estado;

    public Reserva(int id, int idAsistente, int idAlojamiento,
                   int tipoHabitacion, int cantidadPersonas, double precioPorNoche,
                   LocalDate ingreso, LocalDate egreso, EstadoReserva estado) {
        this.id = id;
        this.idAsistente = idAsistente;
        this.idAlojamiento = idAlojamiento;
        setTipoHabitacion(tipoHabitacion);
        setCantidadPersonas(cantidadPersonas);
        setPrecioPorNoche(precioPorNoche);
        setIngreso(ingreso);
        setEgreso(egreso);
        this.estado = estado;
    }

    public Reserva(int id, int idAsistente, int idAlojamiento,
                   LocalDate ingreso, LocalDate egreso, EstadoReserva estado) {
        this(id, idAsistente, idAlojamiento, 0, 1, 0.0, ingreso, egreso, estado);
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

    public int getTipoHabitacion() {
        return tipoHabitacion;
    }

    /**
     * 0 significa "no aplica" (parroquia o espacio); en hoteles va de 1 a 4.
     */
    public void setTipoHabitacion(int tipoHabitacion) {
        if (tipoHabitacion < 0 || tipoHabitacion > Hotel.MAX_PERSONAS_POR_HABITACION) {
            throw new IllegalArgumentException("El tipo de habitacion debe estar entre 0 y "
                    + Hotel.MAX_PERSONAS_POR_HABITACION + ".");
        }
        this.tipoHabitacion = tipoHabitacion;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser positiva.");
        }
        this.cantidadPersonas = cantidadPersonas;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        if (precioPorNoche < 0) {
            throw new IllegalArgumentException("El precio por noche no puede ser negativo.");
        }
        this.precioPorNoche = precioPorNoche;
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

    public boolean seSuperpone(Reserva otra) {

        return this.ingreso.isBefore(otra.egreso) && otra.ingreso.isBefore(this.egreso);
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaActiva() {
        return this.estado == EstadoReserva.ACTIVA;
    }
}
