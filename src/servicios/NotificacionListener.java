package servicios;

import modelo.Notificacion;

/**
 * Avisa a la interfaz cuando el gestor agrega una notificacion nueva. Asi la
 * ventana puede actualizar el contador y mostrar el aviso sin consultar el
 * archivo JSON permanentemente.
 */
public interface NotificacionListener {

    void notificacionAgregada(Notificacion notificacion);
}
