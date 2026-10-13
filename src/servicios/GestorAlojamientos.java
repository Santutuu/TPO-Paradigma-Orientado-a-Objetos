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

    private final GestorUsuarios gestorUsuarios;
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
     * Devuelve las reservas activas del alojamiento que se superponen con el
     * periodo indicado, para consultar los huespedes alojados.
     */
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
            throw new RuntimeException("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
