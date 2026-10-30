package interfaz;

import modelo.Notificacion;
import servicios.GestorNotificaciones;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana no modal con las notificaciones del usuario autenticado. Al cerrarse
 * marca las notificaciones como leidas para que el boton de la ventana
 * principal vuelva a quedar sin contador.
 */
public class VentanaNotificaciones extends JDialog {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    private final GestorNotificaciones gestor;
    private final Runnable alCerrar;
    private final JTextArea area;
    private int idUsuario;

    public VentanaNotificaciones(Window owner, GestorNotificaciones gestor,
                                 int idUsuario, Runnable alCerrar) {
        super(owner, "Notificaciones", ModalityType.MODELESS);
        this.gestor = gestor;
        this.idUsuario = idUsuario;
        this.alCerrar = alCerrar;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO);

        JLabel titulo = new JLabel("Notificaciones");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(AZUL_OSCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        add(titulo, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(area.getFont().deriveFont(Font.PLAIN, 13f));
        area.setForeground(TEXTO);
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 16, 0, 16),
                BorderFactory.createLineBorder(BORDE)));
        add(scroll, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> marcarLeidasYCerrar());
        cerrar.setFont(cerrar.getFont().deriveFont(Font.PLAIN, 13f));
        cerrar.setForeground(Color.WHITE);
        cerrar.setBackground(AZUL_OSCURO);
        cerrar.setOpaque(true);
        cerrar.setFocusPainted(false);
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(cerrar);
        add(pie, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                marcarLeidasYCerrar();
            }
        });

        refrescar();
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
        refrescar();
    }

    /** Vuelve a leer las notificaciones del usuario actual. */
    public void refrescar() {
        List<Notificacion> notificaciones = gestor.notificacionesDe(idUsuario);
        StringBuilder sb = new StringBuilder();
        if (notificaciones.isEmpty()) {
            sb.append("No hay notificaciones.");
        } else {
            for (Notificacion n : notificaciones) {
                sb.append(n.isLeida() ? "  " : "* ");
                sb.append(n.getFechaHora().format(FORMATO))
                        .append(" - ").append(n.getTipo()).append("\n");
                sb.append("  ").append(n.getMensaje()).append("\n\n");
            }
        }
        area.setText(sb.toString());
        area.setCaretPosition(0);
    }

    private void marcarLeidasYCerrar() {
        try {
            gestor.marcarComoLeidas(idUsuario);
        } finally {
            if (alCerrar != null) {
                alCerrar.run();
            }
            dispose();
        }
    }
}
