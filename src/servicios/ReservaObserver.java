package servicios;

import modelo.Reserva;


public interface ReservaObserver {

    default void reservaCreada(Reserva reserva) {

    }

    default void reservaCancelada(Reserva reserva) {

    }
}
