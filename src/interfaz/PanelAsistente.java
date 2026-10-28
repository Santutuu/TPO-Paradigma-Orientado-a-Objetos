package interfaz;

import modelo.Asistente;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Menu del asistente. Solo muestra las operaciones habilitadas para ese rol.
 */
public class PanelAsistente extends JPanel {

    private final VentanaPrincipal ventana;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelAsistente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        // El encabezado identifica al asistente que inició sesión.
        Asistente asistente = (Asistente) ventana.getSesion();
        add(crearEncabezado(asistente.getNombre()), BorderLayout.NORTH);

        // Las opciones del centro llevan a las funciones disponibles para este rol.
        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setOpaque(false);

        acciones.add(botonCentrado("Consultar agenda por fecha", e -> ventana.mostrarAgenda()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Inscribirme a un evento", e -> ventana.mostrarAgenda()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Consultar mis inscripciones y desinscribirme", e -> ventana.mostrarMisInscripciones()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Buscar alojamientos por ciudad", e -> ventana.mostrarBusquedaAlojamientos()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Reservar alojamiento", e -> ventana.mostrarReservas()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Consultar mi reserva", e -> ventana.mostrarReservas()));
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(botonCentrado("Cancelar mi reserva", e -> ventana.mostrarReservas()));

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

        // El pie permite cerrar la sesión o salir de la aplicación.
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

    private JPanel crearEncabezado(String nombre) {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Menu de asistente");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel bienvenida = new JLabel("Bienvenido, " + nombre + ". Gestione sus inscripciones y alojamientos.");
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.PLAIN, 14f));
        bienvenida.setForeground(new Color(220, 230, 239));
        bienvenida.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(bienvenida);
        return encabezado;
    }

    private JButton botonCentrado(String texto, java.awt.event.ActionListener accion) {
        // Crea una opción uniforme y cambia su apariencia al pasar el cursor.
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
