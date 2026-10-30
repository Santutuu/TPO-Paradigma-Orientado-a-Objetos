package modelo;

import java.time.LocalDateTime;

/**
 * Notificacion dirigida a una cuenta. Se guarda en notificaciones.json para
 * que el usuario pueda verla despues de reiniciar la aplicacion.
 */
public class Notificacion {

    public static final String TIPO_EVENTO = "EVENTO";
    public static final String TIPO_RESERVA = "RESERVA";

    private int id;
    private int idDestinatario;
    private LocalDateTime fechaHora;
    private String tipo;
    private String mensaje;
    private boolean leida;

    public Notificacion(int id, int idDestinatario, LocalDateTime fechaHora,
                        String tipo, String mensaje, boolean leida) {
        this.id = id;
        setDestinatario(idDestinatario);
        setFechaHora(fechaHora);
        setTipo(tipo);
        setMensaje(mensaje);
        this.leida = leida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(int idDestinatario) {
        setDestinatario(idDestinatario);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha de la notificacion es obligatoria.");
        }
        this.fechaHora = fechaHora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de notificacion es obligatorio.");
        }
        this.tipo = tipo.trim();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje de la notificacion es obligatorio.");
        }
        this.mensaje = mensaje.trim();
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public void marcarLeida() {
        this.leida = true;
    }

    private void setDestinatario(int idDestinatario) {
        if (idDestinatario <= 0) {
            throw new IllegalArgumentException("El destinatario de la notificacion es obligatorio.");
        }
        this.idDestinatario = idDestinatario;
    }
}
