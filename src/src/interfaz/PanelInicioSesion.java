package interfaz;

import modelo.Administrador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla inicial (sin sesion): permite iniciar sesion, registrarse, consultar
 * la agenda y los alojamientos de forma publica.
 */
public class PanelInicioSesion extends JPanel {

    private final VentanaPrincipal ventana;
    private final JTextField campoUsuario;
    private final JPasswordField campoClave;
    // Colores compartidos para mantener coherente el estilo de esta pantalla.
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelInicioSesion(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        // La pantalla se divide en encabezado, formulario central y acciones públicas.
        add(crearEncabezado(), BorderLayout.NORTH);

        campoUsuario = new JTextField();
        campoClave = new JPasswordField();
        estilizarCampo(campoUsuario);
        estilizarCampo(campoClave);

        // El formulario agrupa las credenciales y el botón que inicia la autenticación.
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.add(crearCampo("Nombre de usuario", campoUsuario));
        formulario.add(Box.createVerticalStrut(14));
        formulario.add(crearCampo("Clave", campoClave));

        JButton botonIngresar = botonAccion("Iniciar sesion", e -> iniciarSesion());
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setBackground(AZUL_OSCURO);
        botonIngresar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        botonIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        botonIngresar.setPreferredSize(new Dimension(560, 46));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        JLabel subtitulo = new JLabel("INICIAR SESION");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.BOLD, 12f));
        subtitulo.setForeground(new Color(99, 116, 133));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(formulario);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(botonIngresar);
        contenido.setPreferredSize(new Dimension(560, contenido.getPreferredSize().height));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        centro.add(contenido);
        add(centro, BorderLayout.CENTER);

        // Estas acciones son públicas y pueden usarse sin iniciar sesión.
        JButton botonRegistroAsistente = botonPie("Registrarse como asistente",
                e -> ventana.mostrarRegistroAsistente());
        JButton botonRegistroAlojamiento = botonPie("Registrarse como alojamiento",
                e -> ventana.mostrarRegistroAlojamiento());
        JButton botonAgenda = botonPie("Ver agenda por fecha",
                e -> ventana.mostrarAgenda());
        JButton botonAlojamientos = botonPie("Ver alojamientos por ciudad",
                e -> ventana.mostrarBusquedaAlojamientos());
        JButton botonSalir = botonPie("Salir", e -> System.exit(0));

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonRegistroAsistente);
        pie.add(botonRegistroAlojamiento);
        pie.add(botonAgenda);
        pie.add(botonAlojamientos);
        pie.add(botonSalir);
        add(pie, BorderLayout.SOUTH);
    }

    // Construye la franja superior con el título y una descripción de la pantalla.
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Bienvenido a la visita del Papa Leon XIV");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Acceda a su cuenta o explore la agenda y los alojamientos disponibles.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Agrupa la etiqueta y su campo para que queden ordenados verticalmente.
    private JPanel crearCampo(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(etiqueta);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setForeground(TEXTO);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(label);
        panel.add(campo);
        return panel;
    }

    // Aplica el aspecto común a los campos de usuario y clave.
    private void estilizarCampo(JTextField campo) {
        campo.setFont(campo.getFont().deriveFont(Font.PLAIN, 14f));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private JButton botonAccion(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 14f));
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // Estiliza los botones del pie y resalta su estado al pasar el cursor.
    private JButton botonPie(String texto, java.awt.event.ActionListener accion) {
        JButton boton = botonAccion(texto, accion);
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 13f));
        boton.setForeground(TEXTO);
        boton.setBackground(Color.WHITE);
        Border bordeNormal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(7, 12, 7, 12));
        Border bordeHover = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(115, 145, 174)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12));
        boton.setBorder(bordeNormal);
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

    private void iniciarSesion() {
        // Valida los datos, autentica la cuenta y abre el menú correspondiente a su rol.
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            UtilidadesInterfaz.mostrarError(this, "Completa usuario y clave.");
            return;
        }

        // Consulta al gestor si las credenciales corresponden a una cuenta registrada.
        Usuario cuenta = ventana.getGestorUsuarios().autenticar(usuario, clave);
        if (cuenta == null) {
            UtilidadesInterfaz.mostrarError(this, "Usuario o clave incorrectos.");
            return;
        }

        // Guarda la sesión y dirige al usuario al menú habilitado para su rol.
        ventana.setSesion(cuenta);
        switch (cuenta.getRol()) {
            case "ASISTENTE":
                ventana.mostrarMenuAsistente();
                break;
            case "ALOJAMIENTO":
                ventana.mostrarMenuAlojamiento();
                break;
            case "ADMINISTRADOR":
                if (!(cuenta instanceof Administrador)) {
                    ventana.setSesion(null);
                    UtilidadesInterfaz.mostrarError(this, "La cuenta no es un administrador valido.");
                    return;
                }
                ventana.mostrarMenuAdministrador();
                break;
            default:
                ventana.setSesion(null);
                UtilidadesInterfaz.mostrarError(this, "Rol desconocido.");
        }
    }
}
