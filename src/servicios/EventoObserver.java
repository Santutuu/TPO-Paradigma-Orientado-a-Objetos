package servicios;

import modelo.Evento;

public interface EventoObserver {

    default void eventoCreado(Evento evento) {
    }

    default void eventoModificado(Evento evento) {
    }

    default void eventoCancelado(Evento evento) {
    }
}
