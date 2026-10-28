package interfaz;

import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Formulario de registro de un alojamiento nuevo (parroquia, hotel o espacio
 * de organizacion). Muestra el campo extra segun el tipo elegido.
 */
public class PanelRegistroAlojamiento extends JPanel {

    private final VentanaPrincipal ventana;
    private final JTextField campoNombreUsuario;
    private final JPasswordField campoClave;
    private final JTextField campoNombre;
    private final JComboBox<Ciudad> comboCiudad;
    private final JTextField campoDireccion;
    private final JTextField campoCapacidad;
    private final JComboBox<String> comboTipo;
    private final JPanel panelExtra;
    private final JTextField campoCategoria;
    private final JTextField campoOrganizador;
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    public PanelRegistroAlojamiento(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearEncabezado(), BorderLayout.NORTH);

        // Se crean los campos compartidos y los datos específicos de cada tipo.
        campoNombreUsuario = new JTextField();
        campoClave = new JPasswordField();
        campoNombre = new JTextField();
        comboCiudad = new JComboBox<>(Ciudad.values());
        campoDireccion = new JTextField();
        campoCapacidad = new JTextField();
        comboTipo = new JComboBox<>(new String[]{"PARROQUIA", "HOTEL", "ESPACIO_ORG"});
        campoCategoria = new JTextField();
        campoOrganizador = new JTextField();

        panelExtra = new JPanel();
        panelExtra.setLayout(new BoxLayout(panelExtra, BoxLayout.Y_AXIS));
        panelExtra.setOpaque(false);
        comboTipo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actualizarCampoExtra();
            }
        });
        actualizarCampoExtra();

        // El formulario central presenta los datos en una columna uniforme.
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.add(crearCampo("Tipo de alojamiento", comboTipo));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Nombre de usuario", campoNombreUsuario));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Clave", campoClave));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Nombre", campoNombre));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Ciudad", comboCiudad));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Dirección", campoDireccion));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearCampo("Capacidad", campoCapacidad));
        formulario.add(panelExtra);

        JButton botonRegistrar = crearBoton("Registrar", true, e -> registrar());
        botonRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        botonRegistrar.setPreferredSize(new Dimension(560, 46));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        JLabel subtitulo = new JLabel("DATOS DEL ALOJAMIENTO");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.BOLD, 12f));
        subtitulo.setForeground(new Color(99, 116, 133));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(formulario);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(botonRegistrar);
        contenido.setPreferredSize(new Dimension(560, contenido.getPreferredSize().height));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        centro.add(contenido);
        JScrollPane desplazamiento = new JScrollPane(centro);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(FONDO);
        add(desplazamiento, BorderLayout.CENTER);

        // El pie permite regresar a la pantalla de inicio.
        JButton botonVolver = crearBoton("Volver", false, e -> ventana.mostrarMenuPrincipal());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonVolver);
        add(pie, BorderLayout.SOUTH);
    }

    // Encabezado azul con el título y una breve indicación para registrarse.
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Registro de alojamiento");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Complete los datos para registrar un alojamiento.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Agrupa la etiqueta y el control correspondiente en una fila visual.
    private JPanel crearCampo(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(etiqueta);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setForeground(TEXTO);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setPreferredSize(new Dimension(560, 36));
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

    // Da el mismo aspecto a los botones principal y secundario.
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

    // Muestra un dato adicional solo cuando el tipo de alojamiento lo necesita.
    private void actualizarCampoExtra() {
        panelExtra.removeAll();
        String tipo = (String) comboTipo.getSelectedItem();
        if ("HOTEL".equals(tipo)) {
            panelExtra.add(crearCampo("Categoria (1 a 5)", campoCategoria));
        } else if ("ESPACIO_ORG".equals(tipo)) {
            panelExtra.add(crearCampo("Organizador", campoOrganizador));
        }
        panelExtra.revalidate();
        panelExtra.repaint();
    }

    private void registrar() {
        try {
            // Lee los valores comunes y luego construye el alojamiento según el tipo elegido.
            String nombreUsuario = campoNombreUsuario.getText().trim();
            String clave = new String(campoClave.getPassword());
            String nombre = campoNombre.getText().trim();
            Ciudad ciudad = (Ciudad) comboCiudad.getSelectedItem();
            String direccion = campoDireccion.getText().trim();
            int capacidad = Integer.parseInt(campoCapacidad.getText().trim());

            if (nombreUsuario.isEmpty() || clave.isEmpty() || nombre.isEmpty() || direccion.isEmpty()) {
                UtilidadesInterfaz.mostrarError(this, "Completa todos los campos.");
                return;
            }

            Alojamiento alojamiento;
            String tipo = (String) comboTipo.getSelectedItem();
            // Cada opción crea el modelo concreto que corresponde al tipo elegido.
            switch (tipo) {
                case "PARROQUIA":
                    alojamiento = new Parroquia(0, nombreUsuario, clave, nombre, ciudad, direccion, capacidad);
                    break;
                case "HOTEL":
                    int categoria = Integer.parseInt(campoCategoria.getText().trim());
                    alojamiento = new Hotel(0, nombreUsuario, clave, nombre, ciudad, direccion, capacidad, categoria);
                    break;
                case "ESPACIO_ORG":
                    String organizador = campoOrganizador.getText().trim();
                    if (organizador.isEmpty()) {
                        throw new IllegalArgumentException("El organizador es obligatorio.");
                    }
                    alojamiento = new EspacioOrg(0, nombreUsuario, clave, nombre, ciudad, direccion, capacidad, organizador);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de alojamiento invalido.");
            }

            // Registra la cuenta y vuelve al inicio cuando la operación termina bien.
            ventana.getGestorUsuarios().registrarAlojamiento(alojamiento);
            UtilidadesInterfaz.mostrarInfo(this, "Alojamiento registrado correctamente.");
            ventana.mostrarMenuPrincipal();
        } catch (NumberFormatException ex) {
            UtilidadesInterfaz.mostrarError(this, "La capacidad y la categoria deben ser numeros enteros.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }
}
