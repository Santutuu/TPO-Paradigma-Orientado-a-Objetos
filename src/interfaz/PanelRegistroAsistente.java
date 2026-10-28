package interfaz;

import modelo.Asistente;
import modelo.Ciudad;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de registro de un asistente nuevo.
 */
public class PanelRegistroAsistente extends JPanel {

    private final VentanaPrincipal ventana;
    private final JTextField campoNombreUsuario;
    private final JPasswordField campoClave;
    private final JTextField campoNombre;
    private final JTextField campoDni;
    private final JComboBox<Ciudad> comboOrigen;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelRegistroAsistente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        // El encabezado, el formulario y el pie mantienen la misma estructura visual.
        add(crearEncabezado(), BorderLayout.NORTH);

        // Campos que contienen los datos requeridos para registrar al asistente.
        campoNombreUsuario = new JTextField();
        campoClave = new JPasswordField();
        campoNombre = new JTextField();
        campoDni = new JTextField();
        comboOrigen = new JComboBox<>(Ciudad.values());

        // El formulario central reúne los datos necesarios para crear la cuenta.
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.add(crearCampo("Nombre de usuario", campoNombreUsuario));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Clave", campoClave));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Nombre completo", campoNombre));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("DNI", campoDni));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearCampo("Ciudad de origen", comboOrigen));

        JButton botonRegistrar = crearBoton("Registrarme", true, e -> registrar());
        botonRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        botonRegistrar.setPreferredSize(new Dimension(560, 46));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        JLabel subtitulo = new JLabel("DATOS DEL ASISTENTE");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.BOLD, 12f));
        subtitulo.setForeground(new Color(99, 116, 133));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(formulario);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(botonRegistrar);
        contenido.setPreferredSize(new Dimension(560, contenido.getPreferredSize().height));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        centro.add(contenido);
        add(centro, BorderLayout.CENTER);

        // El pie permite volver a la pantalla de inicio sin completar el registro.
        JButton botonVolver = crearBoton("Volver", false, e -> ventana.mostrarMenuPrincipal());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonVolver);
        add(pie, BorderLayout.SOUTH);
    }

    // Muestra el título y una indicación breve en la franja superior.
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Registro de asistente");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Complete sus datos para crear una cuenta de asistente.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Coloca cada etiqueta encima de su campo y aplica el estilo común del formulario.
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
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        panel.add(label);
        panel.add(campo);
        return panel;
    }

    // Diferencia visualmente la acción principal de la opción para volver.
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

    private void registrar() {
        try {
            // Valida los datos ingresados y solicita al gestor crear la cuenta.
            String nombreUsuario = campoNombreUsuario.getText().trim();
            String clave = new String(campoClave.getPassword());
            String nombre = campoNombre.getText().trim();
            String dni = campoDni.getText().trim();
            Ciudad origen = (Ciudad) comboOrigen.getSelectedItem();

            if (nombreUsuario.isEmpty() || clave.isEmpty() || nombre.isEmpty() || dni.isEmpty()) {
                UtilidadesInterfaz.mostrarError(this, "Completa todos los campos.");
                return;
            }

            Asistente asistente = new Asistente(0, nombreUsuario, clave, nombre, dni, origen);
            // Si el gestor registra la cuenta, informa el resultado y vuelve al inicio.
            ventana.getGestorUsuarios().registrarAsistente(asistente);
            UtilidadesInterfaz.mostrarInfo(this, "Asistente registrado correctamente.");
            ventana.mostrarMenuPrincipal();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }
}
