package interfaz;

import modelo.Asistente;
import modelo.Evento;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

/**
 * Muestra la agenda de eventos. Segun quien este autenticado permite solo
 * consultar (publico o administrador) o tambien inscribirse y desinscribirse
 * (asistente).
 */
public class PanelAgenda extends JPanel {

    private final VentanaPrincipal ventana;
    private final boolean modoMisInscripciones;
    private final JTable tabla;
    private JComboBox<LocalDate> comboFecha;
    private List<Evento> eventosMostrados;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelAgenda(VentanaPrincipal ventana, boolean modoMisInscripciones) {
        this.ventana = ventana;
        this.modoMisInscripciones = modoMisInscripciones;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        String titulo = modoMisInscripciones ? "Mis inscripciones" : "Agenda de eventos";
        String descripcion = modoMisInscripciones
                ? "Consulta los eventos en los que estas inscripto."
                : "Explora los eventos y filtra la agenda por fecha.";
        add(crearEncabezado(titulo, descripcion), BorderLayout.NORTH);

        // La tabla presenta los eventos disponibles o las inscripciones del asistente.
        tabla = new JTable();
        estilizarTabla();
        JScrollPane desplazamiento = UtilidadesInterfaz.tabla(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(BORDE));
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        centro.add(desplazamiento, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // El pie separa el filtro de fecha de las acciones sobre el evento seleccionado.
        JPanel pie = new JPanel();
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));

        // Cambian los controles según se consulta toda la agenda o las inscripciones propias.
        if (modoMisInscripciones) {
            JButton botonDesinscribir = crearBoton(
                    "Desinscribirme del evento seleccionado", true, e -> desinscribir());
            JPanel acciones = crearPanelAcciones();
            acciones.add(botonDesinscribir);
            pie.add(acciones);
        } else {
            // Las fechas disponibles se presentan en forma legible para evitar errores de escritura.
            comboFecha = new JComboBox<>(new LocalDate[]{
                    LocalDate.of(2026, 11, 8),
                    LocalDate.of(2026, 11, 9),
                    LocalDate.of(2026, 11, 10),
                    LocalDate.of(2026, 11, 11)
            });
            comboFecha.setFont(comboFecha.getFont().deriveFont(Font.PLAIN, 14f));
            comboFecha.setRenderer(new DefaultListCellRenderer() {
                private final DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                        "d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"));

                @Override
                public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                               boolean seleccionado, boolean enfocado) {
                    super.getListCellRendererComponent(lista, valor, indice, seleccionado, enfocado);
                    if (valor instanceof LocalDate) {
                        setText(((LocalDate) valor).format(formato));
                    }
                    return this;
                }
            });
            JButton botonFiltrar = crearBoton("Filtrar por fecha", false, e -> cargarPorFecha());
            JButton botonVerTodos = crearBoton("Ver todos", false, e -> cargarTodos());
            JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            filtro.setOpaque(false);
            filtro.add(new JLabel("Fecha del evento:"));
            filtro.add(comboFecha);
            filtro.add(botonFiltrar);
            filtro.add(botonVerTodos);
            pie.add(filtro);

            if (ventana.getSesion() instanceof Asistente) {
                JButton botonInscribir = crearBoton(
                        "Inscribirme al evento seleccionado", true, e -> inscribir());
                JPanel acciones = crearPanelAcciones();
                acciones.add(botonInscribir);
                pie.add(acciones);
            }
        }

        JPanel accionesFinales = crearPanelAcciones();
        accionesFinales.add(crearBoton("Volver", false, e -> volver()));
        pie.add(accionesFinales);
        add(pie, BorderLayout.SOUTH);

        // Carga inicialmente los datos adecuados para la vista seleccionada.
        if (modoMisInscripciones) {
            cargarInscripciones();
        } else {
            cargarTodos();
        }
    }

    // Encabezado azul que identifica la vista actual y explica su contenido.
    private JPanel crearEncabezado(String tituloTexto, String descripcionTexto) {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel(descripcionTexto);
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Mantiene legibles las filas y hace destacar el encabezado y la selección.
    private void estilizarTabla() {
        tabla.setFont(tabla.getFont().deriveFont(Font.PLAIN, 14f));
        tabla.setRowHeight(30);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);
        tabla.setSelectionBackground(new Color(215, 228, 240));
        tabla.setSelectionForeground(TEXTO);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        tabla.getTableHeader().setBackground(AZUL_OSCURO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel crearPanelAcciones() {
        // Alinea las acciones a la derecha para separarlas visualmente del contenido.
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        acciones.setOpaque(false);
        return acciones;
    }

    private JButton crearBoton(String texto, boolean principal, java.awt.event.ActionListener accion) {
        // Reutiliza un estilo común y permite destacar la acción más importante.
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

    private void volver() {
        // Regresa al menú del usuario actual o al inicio si la agenda es pública.
        if (ventana.getSesion() == null) {
            ventana.mostrarMenuPrincipal();
        } else {
            switch (ventana.getSesion().getRol()) {
                case "ASISTENTE":
                    ventana.mostrarMenuAsistente();
                    break;
                case "ADMINISTRADOR":
                    ventana.mostrarMenuAdministrador();
                    break;
                default:
                    ventana.mostrarMenuPrincipal();
            }
        }
    }

    private void cargarTodos() {
        // Pide al gestor todos los eventos ordenados y actualiza la tabla.
        eventosMostrados = ventana.getGestorEventos().listarTodosOrdenados();
        mostrarTabla();
    }

    private void cargarPorFecha() {
        try {
            // Usa la fecha elegida para pedir al gestor los eventos de ese día.
            LocalDate fecha = (LocalDate) comboFecha.getSelectedItem();
            if (fecha == null) {
                UtilidadesInterfaz.mostrarError(this, "Selecciona una fecha.");
                return;
            }
            eventosMostrados = ventana.getGestorEventos().agendaPorFecha(fecha);
            mostrarTabla();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void cargarInscripciones() {
        // Busca los eventos asociados al asistente y los ordena por fecha y horario.
        Asistente asistente = (Asistente) ventana.getSesion();
        eventosMostrados = new java.util.ArrayList<>();
        for (int idEvento : asistente.getEventos()) {
            Evento evento = ventana.getGestorEventos().buscarPorId(idEvento);
            if (evento != null) {
                eventosMostrados.add(evento);
            }
        }
        eventosMostrados.sort(java.util.Comparator.comparing(Evento::getFecha)
                .thenComparing(Evento::getHorario));
        mostrarTabla();
    }

    private void mostrarTabla() {
        // Convierte la lista actual en filas para el modelo de la tabla.
        String[] columnas = {"Id", "Fecha", "Inicio", "Fin", "Actividad", "Ciudad"};
        Object[][] filas = new Object[eventosMostrados.size()][6];
        for (int i = 0; i < eventosMostrados.size(); i++) {
            Evento e = eventosMostrados.get(i);
            filas[i] = new Object[]{e.getId(), e.getFecha(), e.getHorario(),
                    e.getHorarioFin(), e.getActividad(), e.getCiudad()};
        }
        tabla.setModel(UtilidadesInterfaz.modeloTabla(columnas, filas));
    }

    private Evento eventoSeleccionado() {
        // Las acciones requieren una fila seleccionada para identificar el evento.
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UtilidadesInterfaz.mostrarError(this, "Selecciona un evento de la tabla.");
            return null;
        }
        return eventosMostrados.get(fila);
    }

    private void inscribir() {
        // Solicita la inscripción del asistente al evento elegido e informa errores.
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            return;
        }
        try {
            ventana.getGestorEventos().inscribir((Asistente) ventana.getSesion(), evento.getId());
            UtilidadesInterfaz.mostrarInfo(this, "Te inscribiste al evento \"" + evento.getActividad() + "\".");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void desinscribir() {
        // Quita la inscripción y vuelve a cargar la lista para reflejar el cambio.
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            return;
        }
        try {
            ventana.getGestorEventos().desinscribir((Asistente) ventana.getSesion(), evento.getId());
            UtilidadesInterfaz.mostrarInfo(this, "Te desinscribiste del evento \"" + evento.getActividad() + "\".");
            cargarInscripciones();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }
}
