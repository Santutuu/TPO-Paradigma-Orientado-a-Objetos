package interfaz;

import modelo.Alojamiento;
import modelo.Asistente;
import modelo.Ciudad;
import modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Gestion de reservas del asistente: muestra la reserva actual, permite
 * reservar un alojamiento (eligiendo ciudad, alojamiento y fechas, mostrando la
 * disponibilidad antes de confirmar) y cancelar la reserva vigente.
 */
public class PanelReservas extends JPanel {

    private final VentanaPrincipal ventana;
    private final JComboBox<Ciudad> comboCiudad;
    private final JComboBox<Alojamiento> comboAlojamiento;
    private final JTextField campoIngreso;
    private final JTextField campoEgreso;
    private final JLabel etiquetaDisponibilidad;
    private final JLabel etiquetaReservaActual;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelReservas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearEncabezado(), BorderLayout.NORTH);

        // El contenido queda en una columna y puede desplazarse si no entra en pantalla.
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // La primera sección informa la reserva vigente y permite cancelarla.
        etiquetaReservaActual = new JLabel();
        etiquetaReservaActual.setFont(etiquetaReservaActual.getFont().deriveFont(Font.PLAIN, 14f));
        etiquetaReservaActual.setForeground(TEXTO);
        etiquetaReservaActual.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JPanel tarjetaReserva = crearTarjeta();
        tarjetaReserva.add(etiquetaReservaActual, BorderLayout.CENTER);
        contenido.add(crearTituloSeccion("RESERVA ACTUAL"));
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(tarjetaReserva);
        contenido.add(Box.createVerticalStrut(8));
        JButton botonCancelar = crearBoton("Cancelar mi reserva", false, e -> cancelarReserva());
        JPanel accionesReserva = crearPanelAcciones();
        accionesReserva.add(botonCancelar);
        contenido.add(accionesReserva);
        contenido.add(Box.createVerticalStrut(12));

        // Los controles de búsqueda y fechas se agrupan en el formulario de reserva.
        comboCiudad = new JComboBox<>(Ciudad.values());
        comboAlojamiento = new JComboBox<>();
        campoIngreso = new JTextField("2026-11-08", 12);
        campoEgreso = new JTextField("2026-11-10", 12);
        etiquetaDisponibilidad = new JLabel(" ");
        comboCiudad.setFont(comboCiudad.getFont().deriveFont(Font.PLAIN, 14f));
        comboAlojamiento.setFont(comboAlojamiento.getFont().deriveFont(Font.PLAIN, 14f));

        comboCiudad.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                actualizarAlojamientos();
            }
        });
        actualizarAlojamientos();

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.add(crearCampo("Ciudad", comboCiudad));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Alojamiento", comboAlojamiento));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Ingreso (AAAA-MM-DD)", campoIngreso));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Egreso (AAAA-MM-DD)", campoEgreso));

        JPanel tarjetaFormulario = crearTarjeta();
        tarjetaFormulario.add(formulario, BorderLayout.CENTER);
        contenido.add(crearTituloSeccion("RESERVAR ALOJAMIENTO"));
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(tarjetaFormulario);

        JButton botonVer = crearBoton("Ver disponibilidad", false, e -> verDisponibilidad());
        JButton botonConfirmar = crearBoton("Confirmar reserva", true, e -> confirmarReserva());
        JPanel accionesFormulario = crearPanelAcciones();
        accionesFormulario.add(botonVer);
        accionesFormulario.add(botonConfirmar);
        contenido.add(accionesFormulario);

        etiquetaDisponibilidad.setFont(etiquetaDisponibilidad.getFont().deriveFont(Font.PLAIN, 14f));
        etiquetaDisponibilidad.setForeground(TEXTO);
        etiquetaDisponibilidad.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        etiquetaDisponibilidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(etiquetaDisponibilidad);

        JScrollPane desplazamiento = new JScrollPane(contenido);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(FONDO);
        add(desplazamiento, BorderLayout.CENTER);

        // La navegación queda fija en el pie, separada del formulario.
        JButton botonVolver = crearBoton("Volver", false, e -> ventana.mostrarMenuAsistente());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonVolver);
        add(pie, BorderLayout.SOUTH);

        actualizarReservaActual();
    }

    // Encabezado azul que presenta el objetivo de la pantalla.
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Mis reservas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Consulta tu reserva o busca disponibilidad para alojarte.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Etiqueta visual para separar las partes principales de la pantalla.
    private JLabel crearTituloSeccion(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 12f));
        titulo.setForeground(new Color(99, 116, 133));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return titulo;
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tarjeta;
    }

    // Coloca cada etiqueta encima de su campo y aplica un estilo uniforme.
    private JPanel crearCampo(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(etiqueta);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setForeground(TEXTO);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setPreferredSize(new Dimension(560, 38));
        campo.setFont(campo.getFont().deriveFont(Font.PLAIN, 14f));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        panel.add(label);
        panel.add(campo);
        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        return acciones;
    }

    // Distingue la acción principal de las acciones secundarias.
    private JButton crearBoton(String texto, boolean principal, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 13f));
        boton.setForeground(principal ? Color.WHITE : TEXTO);
        boton.setBackground(principal ? AZUL_OSCURO : Color.WHITE);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(principal ? AZUL_OSCURO : BORDE),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        return boton;
    }

    private Asistente asistente() {
        // La pantalla de reservas se abre para el asistente autenticado.
        return (Asistente) ventana.getSesion();
    }

    private void actualizarReservaActual() {
        // Muestra los datos de la reserva activa o indica que todavía no existe.
        Reserva reserva = ventana.getGestorReservas().reservaActivaDe(asistente().getId());
        if (reserva == null) {
            etiquetaReservaActual.setText("No tenes ninguna reserva activa.");
        } else {
            Alojamiento alojamiento = ventana.getGestorUsuarios().buscarAlojamiento(reserva.getIdAlojamiento());
            etiquetaReservaActual.setText("Reserva activa: " + alojamiento.getNombre()
                    + " del " + reserva.getIngreso() + " al " + reserva.getEgreso() + ".");
        }
    }

    private void actualizarAlojamientos() {
        // Al cambiar de ciudad, renueva la lista de alojamientos disponibles.
        comboAlojamiento.removeAllItems();
        Ciudad ciudad = (Ciudad) comboCiudad.getSelectedItem();
        List<Alojamiento> alojamientos = ventana.getGestorUsuarios().listarAlojamientosPorCiudad(ciudad);
        for (Alojamiento a : alojamientos) {
            comboAlojamiento.addItem(a);
        }
    }

    private void verDisponibilidad() {
        try {
            // Valida el alojamiento y el periodo antes de consultar las plazas libres.
            Alojamiento alojamiento = (Alojamiento) comboAlojamiento.getSelectedItem();
            if (alojamiento == null) {
                UtilidadesInterfaz.mostrarError(this, "No hay alojamientos en esa ciudad.");
                return;
            }
            LocalDate ingreso = UtilidadesInterfaz.leerFecha(campoIngreso.getText());
            LocalDate egreso = UtilidadesInterfaz.leerFecha(campoEgreso.getText());
            if (!egreso.isAfter(ingreso)) {
                throw new IllegalArgumentException("El egreso debe ser posterior al ingreso.");
            }
            int disponibles = ventana.getGestorReservas().disponibilidad(
                    alojamiento.getId(), ingreso, egreso);
            etiquetaDisponibilidad.setText("Disponibilidad para todo el periodo: "
                    + disponibles + " de " + alojamiento.getCapacidad() + " plazas.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void confirmarReserva() {
        try {
            // Envía la solicitud al gestor, que aplica las reglas de negocio y guarda la reserva.
            Alojamiento alojamiento = (Alojamiento) comboAlojamiento.getSelectedItem();
            if (alojamiento == null) {
                UtilidadesInterfaz.mostrarError(this, "No hay alojamientos en esa ciudad.");
                return;
            }
            LocalDate ingreso = UtilidadesInterfaz.leerFecha(campoIngreso.getText());
            LocalDate egreso = UtilidadesInterfaz.leerFecha(campoEgreso.getText());

            ventana.getGestorReservas().crearReserva(asistente().getId(), alojamiento.getId(), ingreso, egreso);
            UtilidadesInterfaz.mostrarInfo(this, "Reserva confirmada en " + alojamiento.getNombre() + ".");
            actualizarReservaActual();
            etiquetaDisponibilidad.setText(" ");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void cancelarReserva() {
        // Pide confirmación antes de cancelar la reserva activa del asistente.
        Reserva reserva = ventana.getGestorReservas().reservaActivaDe(asistente().getId());
        if (reserva == null) {
            UtilidadesInterfaz.mostrarError(this, "No tenes ninguna reserva activa.");
            return;
        }
        if (!UtilidadesInterfaz.confirmar(this, "Seguro que queres cancelar tu reserva?")) {
            return;
        }
        try {
            ventana.getGestorReservas().cancelarReserva(reserva.getId());
            UtilidadesInterfaz.mostrarInfo(this, "Reserva cancelada.");
            actualizarReservaActual();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }
}
