package interfaz;

import modelo.Ciudad;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

/**
 * Menu del administrador. Permite registrar, modificar y consultar eventos.
 */
public class PanelAdministrador extends JPanel {

    private final VentanaPrincipal ventana;

    public PanelAdministrador(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        add(UtilidadesInterfaz.titulo("Menu de administrador"), BorderLayout.NORTH);

        JPanel botones = new JPanel();
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        botones.add(botonCentrado("Registrar evento", e -> registrarEvento()));
        botones.add(botonCentrado("Modificar evento", e -> modificarEvento()));
        botones.add(botonCentrado("Consultar eventos", e -> ventana.mostrarAgenda()));

        add(botones, BorderLayout.CENTER);

        JButton botonCerrarSesion = UtilidadesInterfaz.boton("Cerrar sesion", e -> ventana.cerrarSesion());
        JButton botonSalir = UtilidadesInterfaz.boton("Salir", e -> System.exit(0));
        add(UtilidadesInterfaz.panelBotones(botonCerrarSesion, botonSalir), BorderLayout.SOUTH);
    }

    private void registrarEvento() {
        JTextField campoId = new JTextField("", 10);
        JTextField campoFecha = new JTextField("2026-11-08", 12);
        JTextField campoHorario = new JTextField("10:00", 12);
        JTextField campoActividad = new JTextField("", 20);
        JComboBox<Ciudad> comboCiudad = new JComboBox<>(Ciudad.values());

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Id (numero):", campoId),
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Horario (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudad)
        );

        int opcion = JOptionPane.showConfirmDialog(this, form, "Registrar evento",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(campoId.getText().trim());
            modelo.Evento evento = new modelo.Evento(id,
                    UtilidadesInterfaz.leerFecha(campoFecha.getText()),
                    UtilidadesInterfaz.leerHora(campoHorario.getText()),
                    campoActividad.getText(),
                    (Ciudad) comboCiudad.getSelectedItem());
            ventana.getGestorEventos().registrarEvento(evento);
            UtilidadesInterfaz.mostrarInfo(this, "Evento registrado correctamente.");
        } catch (NumberFormatException ex) {
            UtilidadesInterfaz.mostrarError(this, "El id debe ser un numero entero.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void modificarEvento() {
        JTextField campoId = new JTextField("", 10);
        JPanel formId = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Id del evento:", campoId)
        );
        int opcionId = JOptionPane.showConfirmDialog(this, formId, "Modificar evento - paso 1",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcionId != JOptionPane.OK_OPTION) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(campoId.getText().trim());
        } catch (NumberFormatException ex) {
            UtilidadesInterfaz.mostrarError(this, "El id debe ser un numero entero.");
            return;
        }

        modelo.Evento evento = ventana.getGestorEventos().buscarPorId(id);
        if (evento == null) {
            UtilidadesInterfaz.mostrarError(this, "No existe un evento con id " + id + ".");
            return;
        }

        JTextField campoFecha = new JTextField(evento.getFecha().toString(), 12);
        JTextField campoHorario = new JTextField(evento.getHorario().toString(), 12);
        JTextField campoActividad = new JTextField(evento.getActividad(), 20);
        JComboBox<Ciudad> comboCiudad = new JComboBox<>(Ciudad.values());
        comboCiudad.setSelectedItem(evento.getCiudad());

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Horario (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudad)
        );

        int opcion = JOptionPane.showConfirmDialog(this, form, "Modificar evento - paso 2",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            ventana.getGestorEventos().modificarEvento(id,
                    UtilidadesInterfaz.leerFecha(campoFecha.getText()),
                    UtilidadesInterfaz.leerHora(campoHorario.getText()),
                    campoActividad.getText(),
                    (Ciudad) comboCiudad.getSelectedItem());
            UtilidadesInterfaz.mostrarInfo(this, "Evento modificado correctamente.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private JButton botonCentrado(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height));
        return boton;
    }
}
