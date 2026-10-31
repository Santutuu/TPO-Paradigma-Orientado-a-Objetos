package interfaz;

import modelo.Ciudad;
import modelo.Evento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelAdministrador extends JPanel {

    private final VentanaPrincipal ventana;

    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);
    private static final Color AZUL_CLARO = new Color(226, 236, 244);

    public PanelAdministrador(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        add(UtilidadesInterfaz.titulo("Menu de administrador"), BorderLayout.NORTH);

        JPanel botones = new JPanel();
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        botones.add(botonCentrado("Registrar evento", e -> registrarEvento()));
        botones.add(botonCentrado("Modificar evento", e -> modificarEvento()));
        botones.add(botonCentrado("Cancelar evento", e -> cancelarEvento()));
        botones.add(botonCentrado("Consultar eventos", e -> ventana.mostrarAgenda()));

        add(botones, BorderLayout.CENTER);

        JButton botonCerrarSesion = UtilidadesInterfaz.boton("Cerrar sesion", e -> ventana.cerrarSesion());
        JButton botonSalir = UtilidadesInterfaz.boton("Salir", e -> System.exit(0));
        add(UtilidadesInterfaz.panelBotones(botonCerrarSesion, botonSalir), BorderLayout.SOUTH);
    }

    private void registrarEvento() {
        JTextField campoFecha = new JTextField("2026-11-08", 12);
        JTextField campoHorario = new JTextField("10:00", 12);
        JTextField campoHorarioFin = new JTextField("12:00", 12);
        JTextField campoActividad = new JTextField("", 20);
        JComboBox<Ciudad> comboCiudad = new JComboBox<>(Ciudad.values());
        JTextField campoDireccion = new JTextField("", 20);

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Inicio (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Fin (HH:mm):", campoHorarioFin),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudad),
                UtilidadesInterfaz.fila("Direccion:", campoDireccion)
        );

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("Registrar evento");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(AZUL_OSCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Nuevo evento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            ventana.getGestorEventos().registrar(
                    UtilidadesInterfaz.leerFecha(campoFecha.getText()),
                    UtilidadesInterfaz.leerHora(campoHorario.getText()),
                    UtilidadesInterfaz.leerHora(campoHorarioFin.getText()),
                    campoActividad.getText(),
                    (Ciudad) comboCiudad.getSelectedItem(),
                    campoDireccion.getText());
            UtilidadesInterfaz.mostrarInfo(this, "Evento registrado correctamente.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void modificarEvento() {
        Evento eventoSeleccionado = elegirEvento("Modificar evento",
                "Selecciona el evento a modificar",
                "Usa los filtros para encontrar el evento antes de editarlo.");
        if (eventoSeleccionado == null) {
            return;
        }
        if (!eventoSeleccionado.estaActiva()) {
            UtilidadesInterfaz.mostrarError(this, "No se puede modificar un evento cancelado.");
            return;
        }

        JTextField campoFecha = new JTextField(eventoSeleccionado.getFecha().toString(), 12);
        JTextField campoHorario = new JTextField(eventoSeleccionado.getHorario().toString(), 12);
        JTextField campoHorarioFin = new JTextField(eventoSeleccionado.getHorarioFin().toString(), 12);
        JTextField campoActividad = new JTextField(eventoSeleccionado.getActividad(), 20);
        JComboBox<Ciudad> comboCiudadEdicion = new JComboBox<>(Ciudad.values());
        comboCiudadEdicion.setSelectedItem(eventoSeleccionado.getCiudad());
        JTextField campoDireccionEdicion = new JTextField(eventoSeleccionado.getDireccion(), 20);

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Inicio (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Fin (HH:mm):", campoHorarioFin),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudadEdicion),
                UtilidadesInterfaz.fila("Direccion:", campoDireccionEdicion)
        );

        JPanel panelEdicion = new JPanel(new BorderLayout());
        panelEdicion.setBackground(FONDO);
        panelEdicion.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel cabecera = new JLabel("Evento #" + eventoSeleccionado.getId());
        cabecera.setFont(cabecera.getFont().deriveFont(Font.BOLD, 15f));
        cabecera.setForeground(AZUL_OSCURO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelEdicion.add(cabecera, BorderLayout.NORTH);
        panelEdicion.add(form, BorderLayout.CENTER);

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                panelEdicion,
                "Editar evento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (confirmar != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            ventana.getGestorEventos().modificarEvento(
                    eventoSeleccionado.getId(),
                    UtilidadesInterfaz.leerFecha(campoFecha.getText()),
                    UtilidadesInterfaz.leerHora(campoHorario.getText()),
                    UtilidadesInterfaz.leerHora(campoHorarioFin.getText()),
                    campoActividad.getText(),
                    (Ciudad) comboCiudadEdicion.getSelectedItem(),
                    campoDireccionEdicion.getText()
            );
            UtilidadesInterfaz.mostrarInfo(this, "Evento modificado correctamente.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void cancelarEvento() {
        Evento eventoSeleccionado = elegirEvento("Cancelar evento",
                "Selecciona el evento a cancelar",
                "Solo se pueden cancelar eventos activos. Los inscriptos recibiran un aviso.");
        if (eventoSeleccionado == null) {
            return;
        }
        if (!eventoSeleccionado.estaActiva()) {
            UtilidadesInterfaz.mostrarError(this, "El evento ya esta cancelado.");
            return;
        }
        boolean confirmar = UtilidadesInterfaz.confirmar(this,
                "Cancelar el evento \"" + eventoSeleccionado.getActividad() + "\" del "
                        + eventoSeleccionado.getFecha() + "?\n\n"
                        + "Los asistentes inscriptos recibiran una notificacion.");
        if (!confirmar) {
            return;
        }
        try {
            ventana.getGestorEventos().cancelarEvento(eventoSeleccionado.getId());
            UtilidadesInterfaz.mostrarInfo(this, "Evento cancelado correctamente.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    /**
     * Muestra la tabla de eventos con filtros y devuelve el seleccionado, o
     * null si el usuario cancela el dialogo o no elige una fila.
     */
    private Evento elegirEvento(String tituloDialogo, String tituloTabla, String ayudaTexto) {
        List<Evento> todosLosEventos = ventana.getGestorEventos().listarTodosOrdenados();
        if (todosLosEventos.isEmpty()) {
            UtilidadesInterfaz.mostrarError(this, "Todavia no hay eventos.");
            return null;
        }

        JComboBox<LocalDate> comboFecha = new JComboBox<>();
        comboFecha.addItem(null);
        List<LocalDate> fechas = new ArrayList<>();
        for (Evento e : todosLosEventos) {
            if (!fechas.contains(e.getFecha())) {
                fechas.add(e.getFecha());
                comboFecha.addItem(e.getFecha());
            }
        }

        JComboBox<Ciudad> comboCiudad = new JComboBox<>();
        comboCiudad.addItem(null);
        for (Ciudad ciudad : Ciudad.values()) {
            comboCiudad.addItem(ciudad);
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Fecha", "Inicio", "Fin", "Actividad", "Ciudad",
                        "Direccion", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(tabla.getFont().deriveFont(Font.PLAIN, 13f));
        tabla.setForeground(TEXTO);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(TEXTO);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tabla.getTableHeader().setBackground(AZUL_OSCURO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);

        List<Evento> eventosFiltrados = new ArrayList<>(todosLosEventos);
        cargarEventosEnTabla(modelo, eventosFiltrados);

        JButton botonFiltrar = new JButton("Filtrar");
        botonFiltrar.setFont(botonFiltrar.getFont().deriveFont(Font.PLAIN, 13f));
        botonFiltrar.setForeground(TEXTO);
        botonFiltrar.setBackground(Color.WHITE);
        botonFiltrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        botonFiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonFiltrar.setFocusPainted(false);
        botonFiltrar.addActionListener(e -> {
            LocalDate fechaSeleccionada = (LocalDate) comboFecha.getSelectedItem();
            Ciudad ciudadSeleccionada = (Ciudad) comboCiudad.getSelectedItem();

            eventosFiltrados.clear();
            for (Evento evento : todosLosEventos) {
                boolean coincideFecha = fechaSeleccionada == null || evento.getFecha().equals(fechaSeleccionada);
                boolean coincideCiudad = ciudadSeleccionada == null || evento.getCiudad() == ciudadSeleccionada;
                if (coincideFecha && coincideCiudad) {
                    eventosFiltrados.add(evento);
                }
            }
            cargarEventosEnTabla(modelo, eventosFiltrados);
        });

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panelFiltros.add(new JLabel("Fecha:"));
        panelFiltros.add(comboFecha);
        panelFiltros.add(new JLabel("Ciudad:"));
        panelFiltros.add(comboCiudad);
        panelFiltros.add(botonFiltrar);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(980, 260));
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));

        JPanel panelPadre = new JPanel(new BorderLayout());
        panelPadre.setBackground(FONDO);
        panelPadre.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(tituloTabla);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel ayuda = new JLabel(ayudaTexto);
        ayuda.setFont(ayuda.getFont().deriveFont(Font.PLAIN, 12f));
        ayuda.setForeground(new Color(99, 116, 133));
        ayuda.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        panelPadre.add(titulo, BorderLayout.NORTH);
        panelPadre.add(ayuda, BorderLayout.BEFORE_FIRST_LINE);
        panelPadre.add(panelFiltros, BorderLayout.CENTER);
        panelPadre.add(scroll, BorderLayout.SOUTH);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.add(panelPadre, BorderLayout.NORTH);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                contenedor,
                tituloDialogo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada < 0) {
            UtilidadesInterfaz.mostrarError(this, "Debes seleccionar un evento de la tabla.");
            return null;
        }

        return eventosFiltrados.get(filaSeleccionada);
    }

    private void cargarEventosEnTabla(DefaultTableModel modelo, List<Evento> eventos) {
        modelo.setRowCount(0);
        for (Evento evento : eventos) {
            modelo.addRow(new Object[]{
                    evento.getId(),
                    evento.getFecha(),
                    evento.getHorario(),
                    evento.getHorarioFin(),
                    evento.getActividad(),
                    evento.getCiudad(),
                    evento.getDireccion(),
                    evento.getEstado()
            });
        }
    }

    private JButton botonCentrado(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height));
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 14f));
        boton.setForeground(TEXTO);
        boton.setBackground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}