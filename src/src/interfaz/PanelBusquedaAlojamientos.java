package interfaz;

import modelo.Alojamiento;
import modelo.Asistente;
import modelo.Ciudad;
import modelo.Hotel;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * Muestra los alojamientos disponibles filtrados por ciudad.
 */
public class PanelBusquedaAlojamientos extends JPanel {

    private final VentanaPrincipal ventana;
    private final JComboBox<Ciudad> comboCiudad;
    private final JTable tabla;
    private List<Alojamiento> alojamientosMostrados;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelBusquedaAlojamientos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        // El encabezado y el filtro quedan agrupados en la parte superior.
        JPanel encabezado = crearEncabezado();
        comboCiudad = new JComboBox<>(Ciudad.values());
        comboCiudad.setFont(comboCiudad.getFont().deriveFont(Font.PLAIN, 14f));
        JButton botonBuscar = crearBoton("Buscar", true, e -> buscar());
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        filtro.setBackground(FONDO);
        filtro.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        JLabel etiquetaCiudad = new JLabel("Selecciona una ciudad:");
        etiquetaCiudad.setForeground(TEXTO);
        filtro.add(etiquetaCiudad);
        filtro.add(comboCiudad);
        filtro.add(botonBuscar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(FONDO);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(encabezado);
        norte.add(filtro);

        // La tabla ocupa el centro y se presenta dentro de un margen claro.
        tabla = new JTable();
        estilizarTabla();
        JScrollPane desplazamiento = UtilidadesInterfaz.tabla(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(BORDE));
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 24, 18, 24));
        centro.add(desplazamiento, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // El pie ofrece navegación y, para el asistente, acceso directo a las reservas.
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        sur.setBackground(new Color(235, 239, 244));
        sur.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        if (ventana.getSesion() instanceof Asistente) {
            JButton botonReservar = crearBoton("Ir a reservar", true, e -> ventana.mostrarReservas());
            sur.add(botonReservar);
        }
        JButton botonVolver = crearBoton("Volver", false, e -> volver());
        sur.add(botonVolver);
        add(sur, BorderLayout.SOUTH);

        buscar();
    }

    // Encabezado azul que presenta el propósito de esta pantalla.
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Alojamientos por ciudad");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Busca alojamientos disponibles según su ubicación.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Mejora la lectura de las filas y resalta los encabezados de la tabla.
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

    // Aplica un estilo común a las acciones principal y secundaria.
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
        // Regresa al menú del asistente o a la pantalla pública, según la sesión.
        if (ventana.getSesion() == null) {
            ventana.mostrarMenuPrincipal();
        } else {
            ventana.mostrarMenuAsistente();
        }
    }

    private void buscar() {
        // Consulta los alojamientos de la ciudad elegida y los ordena por nombre.
        Ciudad ciudad = (Ciudad) comboCiudad.getSelectedItem();
        alojamientosMostrados = ventana.getGestorUsuarios().listarAlojamientosPorCiudad(ciudad);
        alojamientosMostrados.sort(Comparator.comparing(Alojamiento::getNombre));

        // Para hoteles muestro precios y habitaciones; en los demas queda "-".
        String[] columnas = {"Nombre", "Tipo", "Descripcion", "Precios por noche", "Habitaciones", "Capacidad"};
        Object[][] filas = new Object[alojamientosMostrados.size()][6];
        for (int i = 0; i < alojamientosMostrados.size(); i++) {
            Alojamiento a = alojamientosMostrados.get(i);
            String precio = "-";
            String habitaciones = "-";
            if (a instanceof Hotel) {
                Hotel hotel = (Hotel) a;
                // El hotel arma los resumenes de precios y habitaciones.
                precio = hotel.resumenPrecios();
                habitaciones = hotel.resumenHabitaciones();
            }
            filas[i] = new Object[]{a.getNombre(), a.getClass().getSimpleName(),
                    a.descripcion(), precio, habitaciones, a.getCapacidad()};
        }
        tabla.setModel(UtilidadesInterfaz.modeloTabla(columnas, filas));
    }
}
