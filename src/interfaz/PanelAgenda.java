package interfaz;

import modelo.Asistente;
import modelo.Evento;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private JButton botonInscribir;
    private JButton botonDesinscribir;
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

        tabla = new JTable();
        estilizarTabla();
        JScrollPane desplazamiento = UtilidadesInterfaz.tabla(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(BORDE));
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        centro.add(desplazamiento, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));

        if (modoMisInscripciones) {
            botonDesinscribir = crearBoton(
                    "Desinscribirme del evento seleccionado", true, e -> desinscribir());
            JPanel acciones = crearPanelAcciones();
            acciones.add(botonDesinscribir);
            pie.add(acciones);
        } else {
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

            if (ventana.getSesion() != null && ventana.getSesion().esAsistente()) {
                botonInscribir = crearBoton(
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

        if (modoMisInscripciones) {
            cargarInscripciones();
        } else {
            cargarTodos();
        }
    }

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
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        acciones.setOpaque(false);
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

    private void volver() {
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
        eventosMostrados = ventana.getGestorEventos().listarTodosOrdenados();
        mostrarTabla();
    }

    private void cargarPorFecha() {
        try {
            LocalDate fecha = (LocalDate) comboFecha.getSelectedItem();
            if (fecha == null) {
                UtilidadesInterfaz.mostrarError(this, "Selecciona una fecha.");
                return;
            }
            eventosMostrados = ventana.getGestorEventos().agendaPorFecha(fecha);
            mostrarTabla();
        } catch (Exception ex) {
            ventana.mostrarError(this, ex);
        }
    }

    private void cargarInscripciones() {
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
        String[] columnas = {"Id", "Fecha", "Inicio", "Fin", "Actividad", "Ciudad",
                "Direccion", "Estado"};
        Object[][] filas = new Object[eventosMostrados.size()][8];
        for (int i = 0; i < eventosMostrados.size(); i++) {
            Evento e = eventosMostrados.get(i);
            filas[i] = new Object[]{e.getId(), e.getFecha(), e.getHorario(),
                    e.getHorarioFin(), e.getActividad(), e.getCiudad(), e.getDireccion(),
                    e.getEstado()};
        }
        tabla.setModel(UtilidadesInterfaz.modeloTabla(columnas, filas));
    }

    private Evento eventoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UtilidadesInterfaz.mostrarError(this, "Selecciona un evento de la tabla.");
            return null;
        }
        return eventosMostrados.get(fila);
    }

    private void inscribir() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            return;
        }
        if (!evento.estaActiva()) {
            UtilidadesInterfaz.mostrarError(this, "El evento esta cancelado.");
            return;
        }
        Asistente asistente = (Asistente) ventana.getSesion();
        ejecutarInscripcion(botonInscribir,
                () -> ventana.getGestorEventos().inscribir(asistente, evento.getId()),
                "Te inscribiste al evento \"" + evento.getActividad() + "\".",
                null);
    }

    private void desinscribir() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            return;
        }
        Asistente asistente = (Asistente) ventana.getSesion();
        ejecutarInscripcion(botonDesinscribir,
                () -> ventana.getGestorEventos().desinscribir(asistente, evento.getId()),
                "Te desinscribiste del evento \"" + evento.getActividad() + "\".",
                this::cargarInscripciones);
    }

    /**
     * Ejecuta la inscripcion o desinscripcion en un hilo de trabajo para no
     * bloquear el EDT. La sincronizacion real vive en GestorEventos.
     */
    private void ejecutarInscripcion(JButton boton, Runnable accion, String mensajeExito,
                                     Runnable alFinalizar) {
        boton.setEnabled(false);
        SwingWorker<Void, Void> trabajador = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                accion.run();
                return null;
            }

            @Override
            protected void done() {
                boton.setEnabled(true);
                try {
                    get();
                    UtilidadesInterfaz.mostrarInfo(PanelAgenda.this, mensajeExito);
                    if (alFinalizar != null) {
                        alFinalizar.run();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    ventana.mostrarError(PanelAgenda.this, ex);
                } catch (ExecutionException ex) {
                    ventana.mostrarError(PanelAgenda.this,
                            ex.getCause() == null ? ex : ex.getCause());
                }
            }
        };
        trabajador.execute();
    }
}
