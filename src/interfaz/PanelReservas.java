package interfaz;

import modelo.Alojamiento;
import modelo.Asistente;
import modelo.Ciudad;
import modelo.Hotel;
import modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Gestion de reservas del asistente: muestra la reserva actual, permite
 * reservar un alojamiento (eligiendo ciudad, alojamiento y fechas, mostrando la
 * disponibilidad antes de confirmar) y cancelar la reserva vigente.
 */
public class PanelReservas extends JPanel {

    private final VentanaPrincipal ventana;
    private final JComboBox<Ciudad> comboCiudad;
    private final JComboBox<Alojamiento> comboAlojamiento;
    private final JComboBox<Integer> comboTipoHabitacion;
    private final JTextField campoPersonas;
    private final JPanel panelHabitacion;
    private final JTextField campoIngreso;
    private final JTextField campoEgreso;
    private final JLabel etiquetaDisponibilidad;
    private final JTextArea areaReservasActivas;
    private final JTextArea areaHistorial;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelReservas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        areaReservasActivas = new JTextArea(3, 40);
        areaReservasActivas.setEditable(false);
        areaReservasActivas.setLineWrap(true);
        areaReservasActivas.setWrapStyleWord(true);
        areaReservasActivas.setFont(areaReservasActivas.getFont().deriveFont(Font.PLAIN, 13f));
        areaReservasActivas.setForeground(TEXTO);
        areaReservasActivas.setBackground(Color.WHITE);
        areaReservasActivas.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scrollReservasActivas = new JScrollPane(areaReservasActivas);
        scrollReservasActivas.setBorder(BorderFactory.createLineBorder(BORDE));
        scrollReservasActivas.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollReservasActivas.setPreferredSize(new Dimension(560, 110));
        scrollReservasActivas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        JPanel tarjetaReserva = crearTarjeta();
        tarjetaReserva.add(scrollReservasActivas, BorderLayout.CENTER);
        contenido.add(crearTituloSeccion("RESERVAS ACTIVAS"));
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(tarjetaReserva);
        contenido.add(Box.createVerticalStrut(8));
        JButton botonCancelar = crearBoton("Cancelar una reserva", false, e -> cancelarReserva());
        JPanel accionesReserva = crearPanelAcciones();
        accionesReserva.add(botonCancelar);
        contenido.add(accionesReserva);
        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearTituloSeccion("RESERVAS CANCELADAS"));
        contenido.add(Box.createVerticalStrut(8));
        areaHistorial = new JTextArea(4, 40);
        areaHistorial.setEditable(false);
        areaHistorial.setLineWrap(true);
        areaHistorial.setWrapStyleWord(true);
        areaHistorial.setFont(areaHistorial.getFont().deriveFont(Font.PLAIN, 13f));
        areaHistorial.setForeground(TEXTO);
        areaHistorial.setBackground(Color.WHITE);
        areaHistorial.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scrollHistorial = new JScrollPane(areaHistorial);
        scrollHistorial.setBorder(BorderFactory.createLineBorder(BORDE));
        scrollHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollHistorial.setPreferredSize(new Dimension(560, 110));
        scrollHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        contenido.add(scrollHistorial);
        contenido.add(Box.createVerticalStrut(16));

        comboCiudad = new JComboBox<>(Ciudad.values());
        comboAlojamiento = new JComboBox<>();
        comboTipoHabitacion = new JComboBox<>();
        campoPersonas = new JTextField("1", 12);
        panelHabitacion = new JPanel();
        panelHabitacion.setLayout(new BoxLayout(panelHabitacion, BoxLayout.Y_AXIS));
        panelHabitacion.setOpaque(false);
        panelHabitacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHabitacion.add(crearCampo("Tipo de habitacion", comboTipoHabitacion));
        panelHabitacion.add(Box.createVerticalStrut(10));
        panelHabitacion.add(crearCampo("Cantidad de personas", campoPersonas));
        panelHabitacion.setVisible(false);
        campoIngreso = new JTextField("2026-11-08", 12);
        campoEgreso = new JTextField("2026-11-10", 12);
        etiquetaDisponibilidad = new JLabel(" ");
        comboCiudad.setFont(comboCiudad.getFont().deriveFont(Font.PLAIN, 14f));
        comboAlojamiento.setFont(comboAlojamiento.getFont().deriveFont(Font.PLAIN, 14f));
        comboTipoHabitacion.setFont(comboTipoHabitacion.getFont().deriveFont(Font.PLAIN, 14f));
        comboTipoHabitacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                           boolean seleccionado, boolean enfocado) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, enfocado);
                if (valor instanceof Integer) {
                    setText(descripcionTipoHabitacion((Integer) valor));
                }
                return this;
            }
        });

        comboCiudad.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                actualizarAlojamientos();
            }
        });
        comboAlojamiento.addActionListener(e -> actualizarHabitacion());
        actualizarAlojamientos();

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.add(crearCampo("Ciudad", comboCiudad));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Alojamiento", comboAlojamiento));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(panelHabitacion);
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

        JButton botonVolver = crearBoton("Volver", false, e -> ventana.mostrarMenuAsistente());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonVolver);
        add(pie, BorderLayout.SOUTH);

        actualizarReservasActivas();
    }

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
        return (Asistente) ventana.getSesion();
    }

    private void actualizarReservasActivas() {
        List<Reserva> activas = ventana.getGestorReservas()
                .reservasActivasDeAsistente(asistente().getId());
        if (activas.isEmpty()) {
            areaReservasActivas.setText("No tenes ninguna reserva activa.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Reserva reserva : activas) {
                sb.append(descripcionReserva(reserva)).append("\n");
            }
            areaReservasActivas.setText(sb.toString());
            areaReservasActivas.setCaretPosition(0);
        }
        actualizarHistorial();
    }

    private String descripcionReserva(Reserva reserva) {
        return "Reserva #" + reserva.getId() + ": " + nombreAlojamiento(reserva)
                + detalleHabitacion(reserva) + " del " + reserva.getIngreso()
                + " al " + reserva.getEgreso() + ".";
    }

    private String nombreAlojamiento(Reserva reserva) {
        Alojamiento alojamiento = ventana.getGestorUsuarios().buscarAlojamiento(reserva.getIdAlojamiento());
        return alojamiento != null ? alojamiento.getNombre()
                : "Alojamiento " + reserva.getIdAlojamiento();
    }

    private String detalleHabitacion(Reserva reserva) {
        if (reserva.getTipoHabitacion() <= 0) {
            return "";
        }
        return " (habitacion para " + reserva.getTipoHabitacion()
                + " personas, " + reserva.getCantidadPersonas()
                + (reserva.getCantidadPersonas() == 1 ? " huesped" : " huespedes")
                + ", $ " + String.format(Locale.ROOT, "%.2f", reserva.getPrecioPorNoche())
                + " por noche)";
    }

    private void actualizarHistorial() {
        StringBuilder sb = new StringBuilder();
        for (Reserva reserva : ventana.getGestorReservas().reservasDeAsistente(asistente().getId())) {
            if (reserva.estaActiva()) {
                continue;
            }
            sb.append("Del ").append(reserva.getIngreso()).append(" al ").append(reserva.getEgreso())
                    .append(" - ").append(nombreAlojamiento(reserva))
                    .append(detalleHabitacion(reserva)).append("\n");
        }
        areaHistorial.setText(sb.length() == 0
                ? "Todavia no tenes reservas canceladas."
                : sb.toString());
        areaHistorial.setCaretPosition(0);
    }

    private void actualizarAlojamientos() {
        comboAlojamiento.removeAllItems();
        Ciudad ciudad = (Ciudad) comboCiudad.getSelectedItem();
        List<Alojamiento> alojamientos = ventana.getGestorUsuarios().listarAlojamientosPorCiudad(ciudad);
        for (Alojamiento a : alojamientos) {
            comboAlojamiento.addItem(a);
        }
        actualizarHabitacion();
    }

    private void actualizarHabitacion() {
        Alojamiento alojamiento = (Alojamiento) comboAlojamiento.getSelectedItem();
        boolean esHotel = alojamiento != null && alojamiento.esHotel();
        panelHabitacion.setVisible(esHotel);
        if (esHotel) {
            Hotel hotel = (Hotel) alojamiento;
            Object anterior = comboTipoHabitacion.getSelectedItem();
            comboTipoHabitacion.removeAllItems();
            for (int tipo = 1; tipo <= Hotel.MAX_PERSONAS_POR_HABITACION; tipo++) {
                if (hotel.getCantidadHabitaciones(tipo) > 0) {
                    comboTipoHabitacion.addItem(tipo);
                }
            }
            if (anterior != null) {
                for (int i = 0; i < comboTipoHabitacion.getItemCount(); i++) {
                    if (comboTipoHabitacion.getItemAt(i).equals(anterior)) {
                        comboTipoHabitacion.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if ((comboTipoHabitacion.getSelectedItem() == null
                    || comboTipoHabitacion.getSelectedIndex() < 0)
                    && comboTipoHabitacion.getItemCount() > 0) {
                comboTipoHabitacion.setSelectedIndex(0);
            }
        }
        if (panelHabitacion.getParent() != null) {
            panelHabitacion.getParent().revalidate();
        }
        revalidate();
        repaint();
    }

    private String descripcionTipoHabitacion(int tipo) {
        return tipo == 1 ? "1 persona" : tipo + " personas";
    }

    private void verDisponibilidad() {
        try {
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
            if (alojamiento.esHotel()) {
                Hotel hotel = (Hotel) alojamiento;
                Integer tipo = (Integer) comboTipoHabitacion.getSelectedItem();
                if (tipo == null) {
                    throw new IllegalArgumentException("El hotel no tiene tipos de habitacion disponibles.");
                }
                int disponibles = Math.max(0, ventana.getGestorReservas()
                        .habitacionesDisponibles(alojamiento.getId(), tipo, ingreso, egreso));
                double precio = hotel.getPrecioPorNoche(tipo);
                long noches = ChronoUnit.DAYS.between(ingreso, egreso);
                etiquetaDisponibilidad.setText("Habitaciones para "
                        + descripcionTipoHabitacion(tipo) + " disponibles: " + disponibles
                        + ". Precio por noche: $ " + String.format(Locale.ROOT, "%.2f", precio)
                        + ". Total estimado: $ "
                        + String.format(Locale.ROOT, "%.2f", precio * noches) + ".");
            } else {
                int disponibles = ventana.getGestorReservas().disponibilidad(
                        alojamiento.getId(), ingreso, egreso);
                etiquetaDisponibilidad.setText("Disponibilidad para todo el periodo: "
                        + disponibles + " de " + alojamiento.getCapacidad() + " plazas.");
            }
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
        }
    }

    private int leerCantidadPersonas() {
        String texto = campoPersonas.getText().trim();
        int personas;
        try {
            personas = Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad de personas debe ser un numero entero.");
        }
        if (personas <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser positiva.");
        }
        return personas;
    }

    private void confirmarReserva() {
        try {
            Alojamiento alojamiento = (Alojamiento) comboAlojamiento.getSelectedItem();
            if (alojamiento == null) {
                UtilidadesInterfaz.mostrarError(this, "No hay alojamientos en esa ciudad.");
                return;
            }
            LocalDate ingreso = UtilidadesInterfaz.leerFecha(campoIngreso.getText());
            LocalDate egreso = UtilidadesInterfaz.leerFecha(campoEgreso.getText());

            if (alojamiento.esHotel()) {
                Integer tipo = (Integer) comboTipoHabitacion.getSelectedItem();
                if (tipo == null) {
                    throw new IllegalArgumentException("Elegi un tipo de habitacion.");
                }
                int personas = leerCantidadPersonas();
                ventana.getGestorReservas().crearReserva(asistente().getId(), alojamiento.getId(),
                        tipo, personas, ingreso, egreso);
                UtilidadesInterfaz.mostrarInfo(this, "Reserva confirmada en " + alojamiento.getNombre()
                        + " (habitacion para " + descripcionTipoHabitacion(tipo)
                        + ", " + personas + (personas == 1 ? " persona" : " personas") + ").");
            } else {
                ventana.getGestorReservas().crearReserva(asistente().getId(), alojamiento.getId(),
                        ingreso, egreso);
                UtilidadesInterfaz.mostrarInfo(this, "Reserva confirmada en " + alojamiento.getNombre() + ".");
            }
            actualizarReservasActivas();
            etiquetaDisponibilidad.setText(" ");
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
        }
    }

    private void cancelarReserva() {
        List<Reserva> activas = ventana.getGestorReservas()
                .reservasActivasDeAsistente(asistente().getId());
        if (activas.isEmpty()) {
            UtilidadesInterfaz.mostrarError(this, "No tenes ninguna reserva activa.");
            return;
        }
        Reserva reserva = activas.get(0);
        if (activas.size() > 1) {
            String[] opciones = new String[activas.size()];
            for (int i = 0; i < activas.size(); i++) {
                opciones[i] = descripcionReserva(activas.get(i));
            }
            String elegida = (String) JOptionPane.showInputDialog(this,
                    "Selecciona la reserva que queres cancelar:",
                    "Cancelar reserva", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegida == null) {
                return;
            }
            int indice = Arrays.asList(opciones).indexOf(elegida);
            if (indice < 0) {
                return;
            }
            reserva = activas.get(indice);
        }
        if (!UtilidadesInterfaz.confirmar(this,
                "Seguro que queres cancelar la reserva #" + reserva.getId()
                        + "? Solo se puede cancelar hasta 24 horas antes del ingreso.")) {
            return;
        }
        try {
            ventana.getGestorReservas().cancelarReservaDeAsistente(asistente().getId(), reserva.getId());
            UtilidadesInterfaz.mostrarInfo(this, "Reserva cancelada.");
            actualizarReservasActivas();
            etiquetaDisponibilidad.setText(" ");
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
        }
    }
}
