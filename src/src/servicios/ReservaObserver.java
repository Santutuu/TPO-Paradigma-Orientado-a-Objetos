package servicios;

import modelo.Reserva;

/**
 * Observador de cambios en las reservas. Permite desacoplar al gestor de las
 * acciones que se ejecutan cuando se crea o cancela una reserva (notificar,
 * registrar, actualizar una vista, etc.).
 *
 * Los metodos tienen implementacion vacia por defecto para que un observador
 * pueda interesarse solo en uno de los eventos.
 */
public interface ReservaObserver {

    default void reservaCreada(Reserva reserva) {
        // Por defecto no hace nada.
    }

    default void reservaCancelada(Reserva reserva) {
        // Por defecto no hace nada.
    }
}
