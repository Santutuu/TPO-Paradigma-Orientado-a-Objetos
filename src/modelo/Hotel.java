package modelo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Alojamiento de tipo hotel. Ademas de la categoria (estrellas), guarda el
 * precio por noche de cada tipo de habitacion y la cantidad de habitaciones
 * agrupadas por capacidad (cuantas personas entran en cada habitacion).
 *
 * La capacidad total del hotel se calcula a partir de las habitaciones:
 * simples + 2 * dobles + 3 * triples + 4 * cuadruples.
 */
public class Hotel extends Alojamiento {


    public static final int CATEGORIA_MINIMA = 1;
    public static final int CATEGORIA_MAXIMA = 5;

    public static final int MAX_PERSONAS_POR_HABITACION = 4;

    private int categoria;
    private final Map<Integer, Double> preciosPorTipo;
    private final Map<Integer, Integer> habitaciones;

    public Hotel(int id, String nombreUsuario, String clave, String nombre,
                 Ciudad ciudad, String direccion, int categoria,
                 double precioSimple, double precioDoble, double precioTriple,
                 double precioCuadruple,
                 int habitacionesSimples, int habitacionesDobles,
                 int habitacionesTriples, int habitacionesCuadruples) {
        super(id, nombreUsuario, clave, nombre, ciudad, direccion,
                capacidadTotal(habitacionesSimples, habitacionesDobles,
                        habitacionesTriples, habitacionesCuadruples));
        setCategoria(categoria);
        this.preciosPorTipo = new LinkedHashMap<>();
        setPrecioPorNoche(1, precioSimple);
        setPrecioPorNoche(2, precioDoble);
        setPrecioPorNoche(3, precioTriple);
        setPrecioPorNoche(4, precioCuadruple);
        this.habitaciones = new LinkedHashMap<>();
        guardarHabitaciones(habitacionesSimples, habitacionesDobles,
                habitacionesTriples, habitacionesCuadruples);
    }

    public Hotel(int id, String nombreUsuario, String clave, String nombre,
                 Ciudad ciudad, String direccion, int categoria, double precioPorNoche,
                 int habitacionesSimples, int habitacionesDobles,
                 int habitacionesTriples, int habitacionesCuadruples) {
        this(id, nombreUsuario, clave, nombre, ciudad, direccion, categoria,
                precioPorNoche, precioPorNoche, precioPorNoche, precioPorNoche,
                habitacionesSimples, habitacionesDobles, habitacionesTriples,
                habitacionesCuadruples);
    }

    public int getCategoria() {
        return categoria;
    }


    public void setCategoria(int categoria) {
        if (categoria < CATEGORIA_MINIMA || categoria > CATEGORIA_MAXIMA) {
            throw new IllegalArgumentException("La categoria debe estar entre "
                    + CATEGORIA_MINIMA + " y " + CATEGORIA_MAXIMA + " estrellas.");
        }
        this.categoria = categoria;
    }

    public double getPrecioPorNoche(int tipoHabitacion) {
        Double precio = preciosPorTipo.get(tipoHabitacion);
        return precio == null ? 0.0 : precio;
    }

    public void setPrecioPorNoche(int tipoHabitacion, double precioPorNoche) {
        validarTipoHabitacion(tipoHabitacion);
        if (precioPorNoche < 0) {
            throw new IllegalArgumentException("El precio por noche no puede ser negativo.");
        }
        preciosPorTipo.put(tipoHabitacion, precioPorNoche);
    }


    public Map<Integer, Double> getPreciosPorTipoHabitacion() {
        return Collections.unmodifiableMap(preciosPorTipo);
    }


    public Map<Integer, Integer> getHabitacionesPorPersonas() {
        return Collections.unmodifiableMap(habitaciones);
    }

    public int getCantidadHabitaciones(int personas) {
        Integer cantidad = habitaciones.get(personas);
        return cantidad == null ? 0 : cantidad;
    }

    /** Cantidad total de habitaciones, sin importar su capacidad. */
    public int getTotalHabitaciones() {
        int total = 0;
        for (int cantidad : habitaciones.values()) {
            total += cantidad;
        }
        return total;
    }

    public void setHabitaciones(int habitacionesSimples, int habitacionesDobles,
                                int habitacionesTriples, int habitacionesCuadruples) {
        int total = capacidadTotal(habitacionesSimples, habitacionesDobles,
                habitacionesTriples, habitacionesCuadruples);
        guardarHabitaciones(habitacionesSimples, habitacionesDobles,
                habitacionesTriples, habitacionesCuadruples);
        setCapacidad(total);
    }

    public String resumenHabitaciones() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entrada : habitaciones.entrySet()) {
            if (entrada.getValue() == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            int personas = entrada.getKey();
            sb.append(personas).append(personas == 1 ? " persona: " : " personas: ")
                    .append(entrada.getValue());
        }
        return sb.toString();
    }

    /** Resumen legible de precios de los tipos que tienen habitaciones. */
    public String resumenPrecios() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Double> entrada : preciosPorTipo.entrySet()) {
            if (getCantidadHabitaciones(entrada.getKey()) == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            int personas = entrada.getKey();
            sb.append(personas).append(personas == 1 ? " persona: " : " personas: ")
                    .append("$").append(String.format(Locale.ROOT, "%.2f", entrada.getValue()));
        }
        return sb.toString();
    }

    public static int capacidadTotal(int simples, int dobles, int triples, int cuadruples) {
        validarCantidad(simples, "simples");
        validarCantidad(dobles, "dobles");
        validarCantidad(triples, "triples");
        validarCantidad(cuadruples, "cuadruples");
        int total = simples + 2 * dobles + 3 * triples + 4 * cuadruples;
        if (total <= 0) {
            throw new IllegalArgumentException("El hotel debe tener al menos una habitacion.");
        }
        return total;
    }

    private static void validarCantidad(int cantidad, String tipo) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de habitaciones " + tipo
                    + " no puede ser negativa.");
        }
    }

    private static void validarTipoHabitacion(int tipoHabitacion) {
        if (tipoHabitacion < 1 || tipoHabitacion > MAX_PERSONAS_POR_HABITACION) {
            throw new IllegalArgumentException("El tipo de habitacion debe estar entre 1 y "
                    + MAX_PERSONAS_POR_HABITACION + ".");
        }
    }

    private void guardarHabitaciones(int simples, int dobles, int triples, int cuadruples) {
        habitaciones.clear();
        habitaciones.put(1, simples);
        habitaciones.put(2, dobles);
        habitaciones.put(3, triples);
        habitaciones.put(4, cuadruples);
    }

    @Override
    public String getTipoCuenta() {
        return "HOTEL";
    }

    @Override
    public boolean esHotel() {
        return true;
    }

    @Override
    public String descripcion() {
        return "Hotel " + getNombre() + " - " + getDireccion() + ", " + getCiudad()
                + " (" + categoria + " estrellas, habitaciones: " + resumenHabitaciones()
                + ", precios: " + resumenPrecios()
                + ", capacidad: " + getCapacidad() + ")";
    }
}
