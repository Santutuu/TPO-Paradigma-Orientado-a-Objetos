package interfaz;

import modelo.Notificacion;
import modelo.Usuario;
import servicios.GestorAlojamientos;
import servicios.GestorEventos;
import servicios.GestorNotificaciones;
import servicios.GestorReservas;
import servicios.GestorUsuarios;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicacion. Centraliza los gestores, la sesion actual
 * y la navegacion entre paneles usando un unico JFrame (sin abrir ventanas nuevas).
 */
public class VentanaPrincipal extends JFrame {

    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color ALERTA = new Color(180, 30, 30);

    private final GestorUsuarios gestorUsuarios;
    private final GestorEventos gestorEventos;
    private final GestorReservas gestorReservas;
    private final GestorAlojamientos gestorAlojamientos;
    private final GestorNotificaciones gestorNotificaciones;

    /** Cuenta autenticada actualmente, o null si no hay sesion. */
    private Usuario sesion;
    private JButton botonNotificaciones;
    private VentanaNotificaciones ventanaNotificaciones;

    public VentanaPrincipal(GestorUsuarios gestorUsuarios, GestorEventos gestorEventos,
                            GestorReservas gestorReservas, GestorAlojamientos gestorAlojamientos,
                            GestorNotificaciones gestorNotificaciones) {
        super("Visita del Papa Leon XIV - Alojamientos y eventos");
        this.gestorUsuarios = gestorUsuarios;
        this.gestorEventos = gestorEventos;
        this.gestorReservas = gestorReservas;
        this.gestorAlojamientos = gestorAlojamientos;
        this.gestorNotificaciones = gestorNotificaciones;

        configurarBarraNotificaciones();
        gestorNotificaciones.agregarListener(this::notificacionAgregada);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null); // centrado en pantalla
        mostrarMenuPrincipal();
        setVisible(true);
    }

    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorEventos getGestorEventos() {
        return gestorEventos;
    }

    public GestorReservas getGestorReservas() {
        return gestorReservas;
    }

    public GestorAlojamientos getGestorAlojamientos() {
        return gestorAlojamientos;
    }

    public Usuario getSesion() {
        return sesion;
    }

    public void setSesion(Usuario sesion) {
        this.sesion = sesion;
        if (sesion == null) {
            cerrarVentanaNotificaciones();
        }
        actualizarBotonNotificaciones();
        if (sesion != null && gestorNotificaciones.contarNoLeidas(sesion.getId()) > 0) {
            // Al iniciar sesion, si hay avisos pendientes, se muestran automaticamente.
            SwingUtilities.invokeLater(this::abrirVentanaNotificaciones);
        }
    }

    private void configurarBarraNotificaciones() {
        JMenuBar barra = new JMenuBar();
        barra.add(Box.createHorizontalGlue());

        botonNotificaciones = new JButton("Notificaciones");
        botonNotificaciones.setFocusPainted(false);
        botonNotificaciones.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonNotificaciones.setForeground(TEXTO);
        botonNotificaciones.addActionListener(e -> abrirVentanaNotificaciones());
        botonNotificaciones.setVisible(false);

        barra.add(botonNotificaciones);
        setJMenuBar(barra);
    }

    private void actualizarBotonNotificaciones() {
        if (botonNotificaciones == null) {
            return;
        }
        if (sesion == null) {
            botonNotificaciones.setVisible(false);
            return;
        }
        int noLeidas = gestorNotificaciones.contarNoLeidas(sesion.getId());
        botonNotificaciones.setVisible(true);
        botonNotificaciones.setText(noLeidas > 0
                ? "Notificaciones (" + noLeidas + ")" : "Notificaciones");
        botonNotificaciones.setFont(botonNotificaciones.getFont().deriveFont(
                noLeidas > 0 ? Font.BOLD : Font.PLAIN));
        botonNotificaciones.setForeground(noLeidas > 0 ? ALERTA : TEXTO);
    }

    private void abrirVentanaNotificaciones() {
        if (sesion == null) {
            return;
        }
        if (ventanaNotificaciones == null) {
            ventanaNotificaciones = new VentanaNotificaciones(this, gestorNotificaciones,
                    sesion.getId(), () -> {
                        actualizarBotonNotificaciones();
                        ventanaNotificaciones = null;
                    });
        } else {
            ventanaNotificaciones.setIdUsuario(sesion.getId());
        }
        ventanaNotificaciones.setVisible(true);
    }

    private void cerrarVentanaNotificaciones() {
        if (ventanaNotificaciones != null) {
            ventanaNotificaciones.dispose();
            ventanaNotificaciones = null;
        }
    }

    private void notificacionAgregada(Notificacion notificacion) {
        if (sesion == null || notificacion.getIdDestinatario() != sesion.getId()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            actualizarBotonNotificaciones();
            if (ventanaNotificaciones != null && ventanaNotificaciones.isVisible()) {
                ventanaNotificaciones.refrescar();
            } else {
                abrirVentanaNotificaciones();
            }
        });
    }

    /** Reemplaza el contenido de la ventana por el panel indicado. */
    private void mostrar(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------------ //
    // NAVEGACION                                                          //
    // ------------------------------------------------------------------ //

    public void mostrarMenuPrincipal() {
        setSesion(null);
        mostrar(new PanelInicioSesion(this));
    }

    public void mostrarRegistroAsistente() {
        mostrar(new PanelRegistroAsistente(this));
    }

    public void mostrarRegistroAlojamiento() {
        mostrar(new PanelRegistroAlojamiento(this));
    }

    public void mostrarMenuAsistente() {
        mostrar(new PanelAsistente(this));
    }

    public void mostrarMenuAlojamiento() {
        mostrar(new PanelAlojamiento(this));
    }

    public void mostrarMenuAdministrador() {
        mostrar(new PanelAdministrador(this));
    }

    public void mostrarAgenda() {
        mostrar(new PanelAgenda(this, false));
    }

    public void mostrarMisInscripciones() {
        mostrar(new PanelAgenda(this, true));
    }

    public void mostrarBusquedaAlojamientos() {
        mostrar(new PanelBusquedaAlojamientos(this));
    }

    public void mostrarReservas() {
        mostrar(new PanelReservas(this));
    }

    public void cerrarSesion() {
        mostrarMenuPrincipal();
    }
}
