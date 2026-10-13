package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Metodos auxiliares compartidos por todos los paneles de la interfaz.
 */
public final class UtilidadesInterfaz {

    private UtilidadesInterfaz() {
        // no se instancia
    }

    /** Muestra un dialogo de error. */
    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un dialogo informativo. */
    public static void mostrarInfo(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Muestra un dialogo de confirmacion y devuelve true si el usuario acepto. */
    public static boolean confirmar(Component padre, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    /** Convierte un texto con formato AAAA-MM-DD en un LocalDate. */
    public static LocalDate leerFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha invalida. Usa el formato AAAA-MM-DD (ej: 2026-11-08).");
        }
    }

    /** Convierte un texto con formato HH:mm en un LocalTime. */
    public static LocalTime leerHora(String texto) {
        try {
            return LocalTime.parse(texto.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Horario invalido. Usa el formato HH:mm (ej: 10:30).");
        }
    }

    /** Crea un titulo con la tipografia de los titulos de pantalla. */
    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 18f));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return etiqueta;
    }

    /** Crea un boton con la accion indicada. */
    public static JButton boton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        return boton;
    }

    /** Crea una fila horizontal con una etiqueta y un campo. */
    public static JPanel fila(String etiqueta, JComponent campo) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel label = new JLabel(etiqueta);
        label.setPreferredSize(new Dimension(140, label.getPreferredSize().height));
        fila.add(label);
        fila.add(campo);
        return fila;
    }

    /** Arma un panel con las filas apiladas verticalmente. */
    public static JPanel formulario(JPanel... filas) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        for (JPanel fila : filas) {
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(fila);
        }
        return panel;
    }

    /** Arma un panel inferior con botones alineados a la izquierda. */
    public static JPanel panelBotones(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        for (JButton boton : botones) {
            panel.add(boton);
        }
        return panel;
    }

    /** Arma una tabla dentro de un panel con barra de desplazamiento. */
    public static JScrollPane tabla(JTable tabla) {
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tabla);
    }

    /** Crea un modelo de tabla de solo lectura a partir de columnas y filas. */
    public static DefaultTableModel modeloTabla(String[] columnas, Object[][] filas) {
        return new DefaultTableModel(filas, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}
