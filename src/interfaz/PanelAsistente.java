package interfaz;

import modelo.Asistente;

import javax.swing.*;
import java.awt.*;

/**
 * Menu del asistente. Solo muestra las operaciones habilitadas para ese rol.
 */
public class PanelAsistente extends JPanel {

    private final VentanaPrincipal ventana;

    public PanelAsistente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());

        Asistente asistente = (Asistente) ventana.getSesion();
        add(UtilidadesInterfaz.titulo("Menu de asistente - " + asistente.getNombre()), BorderLayout.NORTH);

        JPanel botones = new JPanel();
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        botones.add(botonCentrado("Consultar agenda por fecha", e -> ventana.mostrarAgenda()));
        botones.add(botonCentrado("Inscribirme a un evento", e -> ventana.mostrarAgenda()));
        botones.add(botonCentrado("Consultar mis inscripciones y desinscribirme", e -> ventana.mostrarMisInscripciones()));
        botones.add(botonCentrado("Buscar alojamientos por ciudad", e -> ventana.mostrarBusquedaAlojamientos()));
        botones.add(botonCentrado("Reservar alojamiento", e -> ventana.mostrarReservas()));
        botones.add(botonCentrado("Consultar mi reserva", e -> ventana.mostrarReservas()));
        botones.add(botonCentrado("Cancelar mi reserva", e -> ventana.mostrarReservas()));

        add(botones, BorderLayout.CENTER);

        JButton botonCerrarSesion = UtilidadesInterfaz.boton("Cerrar sesion", e -> ventana.cerrarSesion());
        JButton botonSalir = UtilidadesInterfaz.boton("Salir", e -> System.exit(0));
        add(UtilidadesInterfaz.panelBotones(botonCerrarSesion, botonSalir), BorderLayout.SOUTH);
    }

    private JButton botonCentrado(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height));
        return boton;
    }
}
