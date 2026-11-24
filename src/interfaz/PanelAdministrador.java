package interfaz;

import modelo.Ciudad;
import modelo.Evento;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        setBackground(FONDO);

        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setOpaque(false);

        acciones.add(botonCentrado("Registrar evento", e -> registrarEvento()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Modificar evento", e -> modificarEvento()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Cancelar evento", e -> cancelarEvento()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Consultar eventos", e -> ventana.mostrarAgenda()));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        JLabel subtitulo = new JLabel("ACCESOS DISPONIBLES");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.BOLD, 12f));
        subtitulo.setForeground(new Color(99, 116, 133));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(acciones);
        contenido.setPreferredSize(new Dimension(560, contenido.getPreferredSize().height));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.NONE;
        restricciones.anchor = GridBagConstraints.CENTER;
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        centro.add(contenido, restricciones);
        add(centro, BorderLayout.CENTER);

        JButton botonCerrarSesion = UtilidadesInterfaz.boton("Cerrar sesion", e -> ventana.cerrarSesion());
        JButton botonSalir = UtilidadesInterfaz.boton("Salir", e -> System.exit(0));
        estilizarBotonPie(botonCerrarSesion, true);
        estilizarBotonPie(botonSalir, false);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonCerrarSesion);
        pie.add(botonSalir);
        add(pie, BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Menu de administrador");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        String nombre = ventana.getSesion() != null ? ventana.getSesion().getNombre() : "";
        JLabel bienvenida = new JLabel("Bienvenido, " + nombre + ". Gestione los eventos de la visita.");
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.PLAIN, 14f));
        bienvenida.setForeground(new Color(220, 230, 239));
        bienvenida.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(bienvenida);
        return encabezado;
    }

    private void registrarEvento() {
        JTextField campoFecha = new JTextField("2026-11-08", 12);
        JTextField campoHorario = new JTextField("10:00", 12);
        JTextField campoHorarioFin = new JTextField("12:00", 12);
        JTextField campoActividad = new JTextField("", 20);
        JComboBox<Ciudad> comboCiudad = new JComboBox<>(Ciudad.values());
        JTextField campoDireccion = new JTextField("", 20);
        JTextField campoCapacidad = new JTextField("0", 8);

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Inicio (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Fin (HH:mm):", campoHorarioFin),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudad),
                UtilidadesInterfaz.fila("Direccion:", campoDireccion),
                UtilidadesInterfaz.fila("Capacidad maxima (0 = sin limite):", campoCapacidad)
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
                    campoDireccion.getText(),
                    leerCapacidad(campoCapacidad));
            UtilidadesInterfaz.mostrarInfo(this, "Evento registrado correctamente.");
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
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
        JTextField campoCapacidad = new JTextField(
                String.valueOf(eventoSeleccionado.getCapacidadMaxima()), 8);

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Fecha (AAAA-MM-DD):", campoFecha),
                UtilidadesInterfaz.fila("Inicio (HH:mm):", campoHorario),
                UtilidadesInterfaz.fila("Fin (HH:mm):", campoHorarioFin),
                UtilidadesInterfaz.fila("Actividad:", campoActividad),
                UtilidadesInterfaz.fila("Ciudad:", comboCiudadEdicion),
                UtilidadesInterfaz.fila("Direccion:", campoDireccionEdicion),
                UtilidadesInterfaz.fila("Capacidad maxima (0 = sin limite):", campoCapacidad)
        );

        JPanel panelEdicion = new JPanel(new BorderLayout());
        panelEdicion.setBackground(FONDO);
        panelEdicion.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel cabecera = new JLabel("Evento #" + eventoSeleccionado.getId()
                + " - inscriptos: " + ventana.getGestorEventos()
                .contarInscriptos(eventoSeleccionado.getId()));
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
                    campoDireccionEdicion.getText(),
                    leerCapacidad(campoCapacidad)
            );
            UtilidadesInterfaz.mostrarInfo(this, "Evento modificado correctamente.");
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
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
            ventana.mostrarError(this, ex);
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
                        "Direccion", "Estado", "Inscriptos/Cupo"}, 0) {
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
                    evento.getEstado(),
                    cupo(evento)
            });
        }
    }

    /** Lee la capacidad ingresada; vacio o 0 significa sin limite. */
    private int leerCapacidad(JTextField campo) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un numero entero (0 = sin limite).");
        }
    }

    /** Texto de ocupacion para la tabla: inscriptos y capacidad. */
    private String cupo(Evento evento) {
        int inscriptos = ventana.getGestorEventos().contarInscriptos(evento.getId());
        int capacidad = evento.getCapacidadMaxima();
        return inscriptos + " / " + (capacidad == 0 ? "sin limite" : capacidad);
    }

    private JButton botonCentrado(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 14f));
        boton.setForeground(TEXTO);
        boton.setBackground(Color.WHITE);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        Border bordeNormal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(10, 14, 10, 14));
        Border bordeHover = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(115, 145, 174)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14));
        boton.setBorder(bordeNormal);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        boton.setPreferredSize(new Dimension(560, 46));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(232, 239, 247));
                boton.setBorder(bordeHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.WHITE);
                boton.setBorder(bordeNormal);
            }
        });
        return boton;
    }

    private void estilizarBotonPie(JButton boton, boolean principal) {
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 13f));
        boton.setForeground(principal ? Color.WHITE : TEXTO);
        boton.setBackground(principal ? AZUL_OSCURO : Color.WHITE);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(principal ? AZUL_OSCURO : BORDE),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
    }
}