import interfaz.VentanaPrincipal;
import modelo.Evento;
import modelo.Reserva;
import modelo.Usuario;
import persistencia.PersistenciaJSON;
import servicios.GestorAlojamientos;
import servicios.GestorEventos;
import servicios.GestorNotificaciones;
import servicios.GestorReservas;
import servicios.GestorUsuarios;
import servicios.NotificadorReservas;

import javax.swing.*;
import java.util.List;

/**
 * Credenciales de prueba documentadas:
 * - Administrador: admin / admin123
 * - Asistentes: juan, maria, pedro / 1234; Santino / aloha
 * - Alojamientos: sancayetano, sanfrancisco, caritas, sanpablo / 1234
 * - Hoteles: granlujan, hotelcentro / 1234
 */
public class Main {

    public static void main(String[] args) {
        PersistenciaJSON persistencia = new PersistenciaJSON();

        try {
            List<Usuario> usuarios = persistencia.cargarCuentas();
            List<Evento> eventos = persistencia.cargarEventos();
            List<Reserva> reservas = persistencia.cargarReservas();

            GestorUsuarios gestorUsuarios = new GestorUsuarios(usuarios, persistencia);
            GestorEventos gestorEventos = new GestorEventos(eventos, gestorUsuarios, persistencia);
            GestorReservas gestorReservas = new GestorReservas(reservas, gestorUsuarios, persistencia);
            GestorNotificaciones gestorNotificaciones =
                    new GestorNotificaciones(gestorUsuarios, persistencia);
            gestorEventos.agregarObservador(gestorNotificaciones);
            gestorReservas.agregarObservador(gestorNotificaciones);
            // Observador de ejemplo: deja un mensaje cuando se crea o cancela una reserva.
            gestorReservas.agregarObservador(new NotificadorReservas());
            GestorAlojamientos gestorAlojamientos = new GestorAlojamientos(gestorUsuarios, gestorReservas, persistencia);

            SwingUtilities.invokeLater(() -> new VentanaPrincipal(
                    gestorUsuarios, gestorEventos, gestorReservas, gestorAlojamientos,
                    gestorNotificaciones));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo iniciar la aplicacion:\n" + e.getMessage(),
                    "Error de carga", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
