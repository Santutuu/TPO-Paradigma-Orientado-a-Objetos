package interfaz;

import modelo.Alojamiento;
import modelo.Asistente;
import modelo.Ciudad;

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

    public PanelBusquedaAlojamientos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        add(UtilidadesInterfaz.titulo("Alojamientos por ciudad"), BorderLayout.NORTH);

        comboCiudad = new JComboBox<>(Ciudad.values());
        JButton botonBuscar = UtilidadesInterfaz.boton("Buscar", e -> buscar());
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtro.add(new JLabel("Ciudad:"));
        filtro.add(comboCiudad);
        filtro.add(botonBuscar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(filtro, BorderLayout.CENTER);

        tabla = new JTable();
        add(norte, BorderLayout.NORTH);
        add(UtilidadesInterfaz.tabla(tabla), BorderLayout.CENTER);

        JButton botonVolver = UtilidadesInterfaz.boton("Volver", e -> volver());
        JPanel sur = UtilidadesInterfaz.panelBotones(botonVolver);
        if (ventana.getSesion() instanceof Asistente) {
            JButton botonReservar = UtilidadesInterfaz.boton("Ir a reservar", e -> ventana.mostrarReservas());
            sur.add(botonReservar);
        }
        add(sur, BorderLayout.SOUTH);

        buscar();
    }

    private void volver() {
        if (ventana.getSesion() == null) {
            ventana.mostrarMenuPrincipal();
        } else {
            ventana.mostrarMenuAsistente();
        }
    }

    private void buscar() {
        Ciudad ciudad = (Ciudad) comboCiudad.getSelectedItem();
        alojamientosMostrados = ventana.getGestorUsuarios().listarAlojamientosPorCiudad(ciudad);
        alojamientosMostrados.sort(Comparator.comparing(Alojamiento::getNombre));

        String[] columnas = {"Nombre", "Tipo", "Descripcion", "Capacidad"};
        Object[][] filas = new Object[alojamientosMostrados.size()][4];
        for (int i = 0; i < alojamientosMostrados.size(); i++) {
            Alojamiento a = alojamientosMostrados.get(i);
            filas[i] = new Object[]{a.getNombre(), a.getClass().getSimpleName(), a.descripcion(), a.getCapacidad()};
        }
        tabla.setModel(UtilidadesInterfaz.modeloTabla(columnas, filas));
    }
}
