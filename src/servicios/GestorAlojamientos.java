package servicios;

import modelo.*;
import persistencia.ErrorBaseDatos;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorAlojamientos {

    private final GestorUsuarios gestorUsuarios;
    private final GestorReservas gestorReservas;
    private final PersistenciaJSON persistencia;

    public GestorAlojamientos(GestorUsuarios gestorUsuarios, GestorReservas gestorReservas,
                              PersistenciaJSON persistencia) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorReservas = gestorReservas;
        this.persistencia = persistencia;
    }

    public void modificarDatos(int idSesion, int idAlojamiento, String nombre,
                               Ciudad ciudad, String direccion) {
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

    public void modificarCapacidad(int idSesion, int idAlojamiento, int nuevaCapacidad) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (alojamiento.esHotel()) {
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
        if (!alojamiento.esHotel()) {
            throw new IllegalArgumentException("Solo los hoteles tienen precios por tipo de habitacion.");
        }

        Hotel hotel = (Hotel) alojamiento;

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

    public void modificarHabitaciones(int idSesion, int idAlojamiento,
                                      int simples, int dobles, int triples, int cuadruples) {
        Alojamiento alojamiento = gestorUsuarios.buscarAlojamiento(idAlojamiento);
        if (alojamiento == null) {
            throw new IllegalArgumentException("El alojamiento no existe.");
        }
        if (idSesion != idAlojamiento) {
            throw new IllegalArgumentException("Solo podes modificar tus propios datos.");
        }
        if (!alojamiento.esHotel()) {
            throw new IllegalArgumentException("Solo los hoteles tienen habitaciones por capacidad.");
        }

        Hotel hotel = (Hotel) alojamiento;
        int[] cantidades = {simples, dobles, triples, cuadruples};
        Hotel.capacidadTotal(simples, dobles, triples, cuadruples);

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

    public List<Reserva> huespedesEnPeriodo(int idAlojamiento, LocalDate desde, LocalDate hasta) {
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
            throw new ErrorBaseDatos("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
