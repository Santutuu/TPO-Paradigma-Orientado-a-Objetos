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
import java.util.Set;

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
            List<String> faltantes = persistencia.archivosBaseFaltantes();
            if (!faltantes.isEmpty()) {
                mostrarAvisoArchivosFaltantes(persistencia, faltantes);
                System.exit(1);
            }

            Set<Usuario> usuarios = persistencia.cargarCuentas();
            List<Evento> eventos = persistencia.cargarEventos();
            List<Reserva> reservas = persistencia.cargarReservas();

            GestorUsuarios gestorUsuarios = new GestorUsuarios(usuarios, persistencia);
            GestorEventos gestorEventos = new GestorEventos(eventos, gestorUsuarios, persistencia);
            GestorReservas gestorReservas = new GestorReservas(reservas, gestorUsuarios, persistencia);
            GestorNotificaciones gestorNotificaciones =
                    new GestorNotificaciones(gestorUsuarios, persistencia);
            gestorEventos.agregarObservador(gestorNotificaciones);
            gestorReservas.agregarObservador(gestorNotificaciones);
            gestorReservas.agregarObservador(new NotificadorReservas());
            GestorAlojamientos gestorAlojamientos = new GestorAlojamientos(gestorUsuarios, gestorReservas, persistencia);

            SwingUtilities.invokeLater(() -> new VentanaPrincipal(
                    gestorUsuarios, gestorEventos, gestorReservas, gestorAlojamientos,
                    gestorNotificaciones));
        } catch (Exception e) {
            String detalle = e.getMessage();
            if (detalle == null || detalle.trim().isEmpty()) {
                detalle = e.getClass().getSimpleName();
            }
            JOptionPane.showMessageDialog(null,
                    "No se pudo iniciar la aplicacion.\n\n" + detalle
                            + "\n\nCarpeta de datos:\n" + persistencia.carpetaDatos(),
                    "Error de inicio (Administrador)", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /** Avisa que archivos base faltan y donde deberian estar. */
    private static void mostrarAvisoArchivosFaltantes(PersistenciaJSON persistencia,
                                                      List<String> faltantes) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Faltan archivos de datos obligatorios en:\n");
        mensaje.append(persistencia.carpetaDatos()).append("\n\n");
        for (String nombre : faltantes) {
            mensaje.append("  - ").append(nombre).append("\n");
        }
        mensaje.append("\nRestaura los archivos desde la carpeta datos/ del proyecto\n");
        mensaje.append("y ejecuta la aplicacion desde la raiz.\n\n");
        mensaje.append("notificaciones.json es opcional: se crea automaticamente.");

        JOptionPane.showMessageDialog(null, mensaje.toString(),
                "Faltan archivos de datos (Administrador)", JOptionPane.ERROR_MESSAGE);
    }
}
