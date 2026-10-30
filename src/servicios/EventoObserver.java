package servicios;

import modelo.Evento;

/**
 * Observador de cambios en la agenda. Permite que las notificaciones y otras
 * acciones reaccionen al alta o a la modificacion de un evento sin acoplar al
 * gestor con la interfaz.
 */
public interface EventoObserver {

    default void eventoCreado(Evento evento) {
        // Por defecto no hace nada.
    }

    default void eventoModificado(Evento evento) {
        // Por defecto no hace nada.
    }
}
