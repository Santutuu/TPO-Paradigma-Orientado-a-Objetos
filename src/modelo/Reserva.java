package modelo;

import java.time.LocalDate;

/**
 * Reserva de un alojamiento hecha por un asistente. Guarda identificadores en
 * lugar de los objetos completos, tal como indica el UML.
 *
 * En hoteles, una reserva ocupa una habitacion completa: guarda el tipo de
 * habitacion (capacidad en personas), cuantas personas se alojan y el precio
 * por noche vigente al momento de reservar. En parroquias y espacios de
 * organizacion el tipo es 0, la cantidad de personas es 1 y el precio es 0.
 */
public class Reserva {

    // Para relacionar la reserva uso ids, como pide el UML.
    private int id;
    private int idAsistente;
    private int idAlojamiento;
    // Estos tres datos son para hoteles. En parroquias/espacios quedan 0, 1 y 0.
    private int tipoHabitacion;
    private int cantidadPersonas;
    private double precioPorNoche;
    private LocalDate ingreso;
    private LocalDate egreso;
    private EstadoReserva estado;

    // Constructor completo: lo usan los hoteles y tambien la persistencia.
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

    /** Constructor compatible para reservas sin datos de hotel. */
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

    /**
     * Determina si dos reservas se superponen en el tiempo. El ingreso es
     * inclusivo y el egreso es exclusivo: si una reserva termina el mismo dia
     * en que empieza la otra, no se consideran superpuestas.
     */
    public boolean seSuperpone(Reserva otra) {
        // El ingreso cuenta y el egreso no. Por eso una reserva que termina
        // el mismo dia que empieza otra no se considera superpuesta.
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
