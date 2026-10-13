package interfaz;

import modelo.Usuario;
import servicios.GestorAlojamientos;
import servicios.GestorEventos;
import servicios.GestorReservas;
import servicios.GestorUsuarios;

import javax.swing.*;

/**
 * Ventana principal de la aplicacion. Centraliza los gestores, la sesion actual
 * y la navegacion entre paneles usando un unico JFrame (sin abrir ventanas nuevas).
 */
public class VentanaPrincipal extends JFrame {

    private final GestorUsuarios gestorUsuarios;
    private final GestorEventos gestorEventos;
    private final GestorReservas gestorReservas;
    private final GestorAlojamientos gestorAlojamientos;

    /** Cuenta autenticada actualmente, o null si no hay sesion. */
    private Usuario sesion;

    public VentanaPrincipal(GestorUsuarios gestorUsuarios, GestorEventos gestorEventos,
                            GestorReservas gestorReservas, GestorAlojamientos gestorAlojamientos) {
        super("Visita del Papa Leon XIV - Alojamientos y eventos");
        this.gestorUsuarios = gestorUsuarios;
        this.gestorEventos = gestorEventos;
        this.gestorReservas = gestorReservas;
        this.gestorAlojamientos = gestorAlojamientos;

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
        sesion = null;
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
