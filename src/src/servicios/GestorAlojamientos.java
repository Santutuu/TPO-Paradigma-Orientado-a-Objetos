package servicios;

import modelo.*;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la consulta y modificacion de datos y capacidad de los alojamientos,
 * respetando las reservas vigentes.
 */
public class GestorAlojamientos {

    // Necesito los usuarios para buscar el alojamiento y guardar los cambios.
    private final GestorUsuarios gestorUsuarios;
    // Y las reservas para no bajar la capacidad o las habitaciones de mas.
    private final GestorReservas gestorReservas;
    private final PersistenciaJSON persistencia;

    public GestorAlojamientos(GestorUsuarios gestorUsuarios, GestorReservas gestorReservas,
                              PersistenciaJSON persistencia) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorReservas = gestorReservas;
        this.persistencia = persistencia;
    }

    /**
     * Modifica los datos propios de un alojamiento. El parametro "idSesion" es
     * el id de la cuenta autenticada, para garantizar que solo modifique sus datos.
     */
    public void modificarDatos(int idSesion, int idAlojamiento, String nombre,
                               Ciudad ciudad, String direccion) {
        // Comparo el id de la sesion con el del alojamiento para que nadie
        // pueda modificar los datos de otro.
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion es obligatoria.");
        }
        alojamiento.setNombre(nombre.trim());
        alojamiento.setCiudad(ciudad);
        alojamiento.setDireccion(direccion.trim());
        guardarCuentas();
    }

    /**
     * Modifica la capacidad de un alojamiento, impidiendo que quede por debajo
     * de la ocupacion existente por reservas activas.
     */
    public void modificarCapacidad(int idSesion, int idAlojamiento, int nuevaCapacidad) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (alojamiento instanceof Hotel) {
            // La capacidad del hotel sale de sus habitaciones, asi que no se
            // cambia directo: hay que editar las habitaciones.
            throw new IllegalArgumentException("En un hotel la capacidad se calcula a partir "
                    + "de las habitaciones. Modifica la cantidad de habitaciones.");
        }
        if (nuevaCapacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un numero positivo.");
        }
        int ocupacionMaxima = gestorReservas.ocupacionMaxima(idAlojamiento);
        if (nuevaCapacidad < ocupacionMaxima) {
            throw new IllegalArgumentException("No se puede reducir la capacidad a " + nuevaCapacidad
                    + " porque hay una ocupacion maxima de " + ocupacionMaxima + " por reservas activas.");
        }
        alojamiento.setCapacidad(nuevaCapacidad);
        guardarCuentas();
    }

    /**
     * Modifica los precios por noche de cada tipo de habitacion del hotel.
     */
    public void modificarPrecios(int idSesion, int idAlojamiento,
                                 double precioSimple, double precioDoble,
                                 double precioTriple, double precioCuadruple) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (!(alojamiento instanceof Hotel)) {
            throw new IllegalArgumentException("Solo los hoteles tienen precios por tipo de habitacion.");
        }

        Hotel hotel = (Hotel) alojamiento;
        // Paso los cuatro precios a un arreglo para recorrerlos comodo.
        double[] precios = {precioSimple, precioDoble, precioTriple, precioCuadruple};
        for (int i = 0; i < precios.length; i++) {
            int tipo = i + 1;
            String descripcion = tipo == 1 ? "1 persona" : tipo + " personas";
            if (precios[i] < 0) {
                throw new IllegalArgumentException("El precio para " + descripcion + " no puede ser negativo.");
            }
            if (hotel.getCantidadHabitaciones(tipo) > 0 && precios[i] <= 0) {
                throw new IllegalArgumentException("El precio para " + descripcion
                        + " debe ser mayor a cero porque el hotel tiene ese tipo de habitacion.");
            }
        }

        for (int i = 0; i < precios.length; i++) {
            hotel.setPrecioPorNoche(i + 1, precios[i]);
        }
        guardarCuentas();
    }

    /**
     * Modifica la distribucion de habitaciones de un hotel y recalcula su
     * capacidad. Por cada tipo, no permite dejar menos habitaciones que la
     * ocupacion maxima simultanea de ese tipo.
     */
    public void modificarHabitaciones(int idSesion, int idAlojamiento,
                                      int simples, int dobles, int triples, int cuadruples) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (!(alojamiento instanceof Hotel)) {
            throw new IllegalArgumentException("Solo los hoteles tienen habitaciones por capacidad.");
        }

        Hotel hotel = (Hotel) alojamiento;
        int[] cantidades = {simples, dobles, triples, cuadruples};
        // Valida las cantidades y la capacidad resultante (no puede ser cero).
        Hotel.capacidadTotal(simples, dobles, triples, cuadruples);

        // Reviso tipo por tipo que no saque habitaciones que ya estan ocupadas.

        for (int i = 0; i < cantidades.length; i++) {
            int tipo = i + 1;
            String descripcion = tipo == 1 ? "1 persona" : tipo + " personas";
            int ocupadas = gestorReservas.ocupacionMaximaHabitaciones(idAlojamiento, tipo);
            if (cantidades[i] < ocupadas) {
                throw new IllegalArgumentException("No se pueden dejar " + cantidades[i]
                        + " habitaciones para " + descripcion + " porque hay "
                        + ocupadas + " ocupadas simultaneamente por reservas activas.");
            }
            if (cantidades[i] > 0 && hotel.getPrecioPorNoche(tipo) <= 0) {
                throw new IllegalArgumentException("Configura el precio para " + descripcion
                        + " antes de habilitar ese tipo de habitacion.");
            }
        }

        hotel.setHabitaciones(simples, dobles, triples, cuadruples);
        guardarCuentas();
    }

    /**
     * Devuelve las reservas activas del alojamiento que se superponen con el
     * periodo indicado, para consultar los huespedes alojados.
     */
    public List<Reserva> huespedesEnPeriodo(int idAlojamiento, LocalDate desde, LocalDate hasta) {
        // Me quedo con las reservas activas que tocan el periodo elegido.
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : gestorReservas.reservasDeAlojamiento(idAlojamiento)) {
            if (r.estaActiva() && r.getIngreso().isBefore(hasta) && r.getEgreso().isAfter(desde)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    private void guardarCuentas() {
        try {
            persistencia.guardarCuentas(gestorUsuarios.getUsuarios());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
