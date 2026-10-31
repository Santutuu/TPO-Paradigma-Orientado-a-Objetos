package interfaz;

import modelo.Alojamiento;
import modelo.Ciudad;
import modelo.Hotel;
import modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Menu del alojamiento. Permite consultar y modificar sus datos, su capacidad
 * y consultar los huespedes alojados en un periodo.
 */
public class PanelAlojamiento extends JPanel {

    private static final Color AZUL_OSCURO = new Color(31, 55, 79);
    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(44, 58, 72);
    private static final Color BORDE = new Color(215, 222, 230);

    private final VentanaPrincipal ventana;

    public PanelAlojamiento(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        Alojamiento alojamiento = (Alojamiento) ventana.getSesion();
        add(crearEncabezado(alojamiento.getNombre()), BorderLayout.NORTH);

        // Resume la información del alojamiento y deja sus operaciones principales a mano.
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        contenido.add(crearTituloSeccion("MIS DATOS"));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(crearTarjetaDatos(alojamiento));
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(crearTituloSeccion("OPERACIONES"));
        contenido.add(Box.createVerticalStrut(12));

        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        acciones.add(crearBotonMenu("Modificar mis datos", e -> modificarDatos()));
        acciones.add(Box.createVerticalStrut(8));
        if (alojamiento instanceof Hotel) {
            // Un hotel no tiene una capacidad suelta: edita precios y
            // habitaciones por tipo.
            acciones.add(crearBotonMenu("Modificar precios por tipo", e -> modificarPrecios()));
            acciones.add(Box.createVerticalStrut(8));
            acciones.add(crearBotonMenu("Modificar habitaciones", e -> modificarHabitaciones()));
        } else {
            acciones.add(crearBotonMenu("Modificar capacidad", e -> modificarCapacidad()));
        }
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(crearBotonMenu("Consultar huespedes en un periodo", e -> consultarHuespedes()));
        contenido.add(acciones);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        centro.add(contenido);
        add(centro, BorderLayout.CENTER);

        // Las acciones de sesión se mantienen separadas del menú del alojamiento.
        JButton botonCerrarSesion = crearBoton("Cerrar sesion", true, e -> ventana.cerrarSesion());
        JButton botonSalir = crearBoton("Salir", false, e -> System.exit(0));
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pie.setBackground(new Color(235, 239, 244));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));
        pie.add(botonCerrarSesion);
        pie.add(botonSalir);
        add(pie, BorderLayout.SOUTH);
    }

    // Encabezado azul con el nombre del alojamiento autenticado.
    private JPanel crearEncabezado(String nombre) {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel titulo = new JLabel("Menu de alojamiento");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 23f));
        titulo.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Bienvenido, " + nombre + ". Gestione sus datos y consulte sus huespedes.");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 14f));
        descripcion.setForeground(new Color(220, 230, 239));
        descripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        encabezado.add(titulo);
        encabezado.add(descripcion);
        return encabezado;
    }

    // Presenta la descripción del modelo en una tarjeta simple de solo lectura.
    private JPanel crearTarjetaDatos(Alojamiento alojamiento) {
        JTextArea area = new JTextArea(alojamiento.descripcion());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(area.getFont().deriveFont(Font.PLAIN, 14f));
        area.setForeground(TEXTO);
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createLineBorder(BORDE));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(area, BorderLayout.CENTER);
        tarjeta.setPreferredSize(new Dimension(560, 90));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        return tarjeta;
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 12f));
        titulo.setForeground(new Color(99, 116, 133));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return titulo;
    }

    // Cada opción del menú abre el formulario correspondiente.
    private JButton crearBotonMenu(String texto, java.awt.event.ActionListener accion) {
        JButton boton = crearBoton(texto, false, accion);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        boton.setPreferredSize(new Dimension(560, 46));
        return boton;
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

    private JPanel crearCampo(String etiqueta, JComponent campo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(etiqueta);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setForeground(TEXTO);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setPreferredSize(new Dimension(360, 38));
        campo.setFont(campo.getFont().deriveFont(Font.PLAIN, 14f));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        fila.add(label);
        fila.add(campo);
        return fila;
    }

    private JPanel crearFormulario(JPanel... campos) {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                formulario.add(Box.createVerticalStrut(10));
            }
            formulario.add(campos[i]);
        }
        return formulario;
    }

    private void modificarDatos() {
        Alojamiento alojamiento = (Alojamiento) ventana.getSesion();
        JTextField campoNombre = new JTextField(alojamiento.getNombre());
        JTextField campoDireccion = new JTextField(alojamiento.getDireccion());
        JComboBox<Ciudad> comboCiudad = new JComboBox<>(Ciudad.values());
        comboCiudad.setSelectedItem(alojamiento.getCiudad());

        // Los campos aparecen precargados para facilitar la edición de los datos actuales.
        JPanel formulario = crearFormulario(
                crearCampo("Nombre", campoNombre),
                crearCampo("Ciudad", comboCiudad),
                crearCampo("Direccion", campoDireccion));
        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Modificar mis datos",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            ventana.getGestorAlojamientos().modificarDatos(
                    alojamiento.getId(), alojamiento.getId(), campoNombre.getText(),
                    (Ciudad) comboCiudad.getSelectedItem(), campoDireccion.getText());
            UtilidadesInterfaz.mostrarInfo(this, "Datos actualizados correctamente.");
            ventana.mostrarMenuAlojamiento();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private void modificarCapacidad() {
        Alojamiento alojamiento = (Alojamiento) ventana.getSesion();
        JTextField campoCapacidad = new JTextField(String.valueOf(alojamiento.getCapacidad()));
        JPanel formulario = crearFormulario(crearCampo("Nueva capacidad", campoCapacidad));

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Modificar capacidad",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int capacidad = Integer.parseInt(campoCapacidad.getText().trim());
            ventana.getGestorAlojamientos().modificarCapacidad(
                    alojamiento.getId(), alojamiento.getId(), capacidad);
            UtilidadesInterfaz.mostrarInfo(this, "Capacidad actualizada correctamente.");
            ventana.mostrarMenuAlojamiento();
        } catch (NumberFormatException ex) {
            UtilidadesInterfaz.mostrarError(this, "La capacidad debe ser un numero entero.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    // Muestro los cuatro precios actuales y mando los nuevos al gestor.
    private void modificarPrecios() {
        Hotel hotel = (Hotel) ventana.getSesion();
        JTextField campo1 = new JTextField(String.valueOf(hotel.getPrecioPorNoche(1)));
        JTextField campo2 = new JTextField(String.valueOf(hotel.getPrecioPorNoche(2)));
        JTextField campo3 = new JTextField(String.valueOf(hotel.getPrecioPorNoche(3)));
        JTextField campo4 = new JTextField(String.valueOf(hotel.getPrecioPorNoche(4)));

        JPanel formulario = crearFormulario(
                crearCampo("Precio habitacion simple", campo1),
                crearCampo("Precio habitacion doble", campo2),
                crearCampo("Precio habitacion triple", campo3),
                crearCampo("Precio habitacion cuadruple", campo4));

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Modificar precios por tipo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double precio1 = leerPrecio(campo1);
            double precio2 = leerPrecio(campo2);
            double precio3 = leerPrecio(campo3);
            double precio4 = leerPrecio(campo4);
            ventana.getGestorAlojamientos().modificarPrecios(
                    hotel.getId(), hotel.getId(), precio1, precio2, precio3, precio4);
            UtilidadesInterfaz.mostrarInfo(this, "Precios actualizados correctamente.");
            ventana.mostrarMenuAlojamiento();
        } catch (NumberFormatException ex) {
            UtilidadesInterfaz.mostrarError(this, "Los precios deben ser numeros validos.");
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private double leerPrecio(JTextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(',', '.'));
    }

    // Pido cuantas habitaciones hay de cada tipo y actualizo el hotel.
    private void modificarHabitaciones() {
        Hotel hotel = (Hotel) ventana.getSesion();
        JTextField campo1 = new JTextField(String.valueOf(hotel.getCantidadHabitaciones(1)));
        JTextField campo2 = new JTextField(String.valueOf(hotel.getCantidadHabitaciones(2)));
        JTextField campo3 = new JTextField(String.valueOf(hotel.getCantidadHabitaciones(3)));
        JTextField campo4 = new JTextField(String.valueOf(hotel.getCantidadHabitaciones(4)));

        JPanel formulario = crearFormulario(
                crearCampo("Habitaciones para 1 persona", campo1),
                crearCampo("Habitaciones para 2 personas", campo2),
                crearCampo("Habitaciones para 3 personas", campo3),
                crearCampo("Habitaciones para 4 personas", campo4));

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Modificar habitaciones",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int simples = leerCantidadHabitaciones(campo1, "1 persona");
            int dobles = leerCantidadHabitaciones(campo2, "2 personas");
            int triples = leerCantidadHabitaciones(campo3, "3 personas");
            int cuadruples = leerCantidadHabitaciones(campo4, "4 personas");
            ventana.getGestorAlojamientos().modificarHabitaciones(
                    hotel.getId(), hotel.getId(), simples, dobles, triples, cuadruples);
            UtilidadesInterfaz.mostrarInfo(this, "Habitaciones actualizadas correctamente.");
            ventana.mostrarMenuAlojamiento();
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    // Las cantidades no pueden ser negativas; si esta vacio tomo 0.
    private int leerCantidadHabitaciones(JTextField campo, String etiqueta) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            return 0;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad de habitaciones para "
                    + etiqueta + " debe ser un numero entero.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de habitaciones para "
                    + etiqueta + " no puede ser negativa.");
        }
        return cantidad;
    }

    private void consultarHuespedes() {
        Alojamiento alojamiento = (Alojamiento) ventana.getSesion();

        // Los selectores limitan el periodo a las fechas disponibles del evento.
        JComboBox<LocalDate> comboDesde = crearSelectorFecha();
        JComboBox<LocalDate> comboHasta = crearSelectorFecha();
        comboDesde.setSelectedIndex(0);
        comboHasta.setSelectedIndex(3);
        JPanel formulario = crearFormulario(
                crearCampo("Desde", comboDesde),
                crearCampo("Hasta", comboHasta));

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Consultar huespedes",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate desde = (LocalDate) comboDesde.getSelectedItem();
            LocalDate hasta = (LocalDate) comboHasta.getSelectedItem();
            if (desde == null || hasta == null) {
                UtilidadesInterfaz.mostrarError(this, "Selecciona las fechas del periodo.");
                return;
            }
            if (!hasta.isAfter(desde)) {
                UtilidadesInterfaz.mostrarError(this, "La fecha final debe ser posterior a la inicial.");
                return;
            }

            List<Reserva> huespedes = ventana.getGestorAlojamientos()
                    .huespedesEnPeriodo(alojamiento.getId(), desde, hasta);
            mostrarHuespedes(huespedes, desde, hasta);
        } catch (Exception ex) {
            UtilidadesInterfaz.mostrarError(this, ex.getMessage());
        }
    }

    private JComboBox<LocalDate> crearSelectorFecha() {
        JComboBox<LocalDate> selector = new JComboBox<>(new LocalDate[]{
                LocalDate.of(2026, 11, 8),
                LocalDate.of(2026, 11, 9),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11)
        });
        selector.setFont(selector.getFont().deriveFont(Font.PLAIN, 14f));
        selector.setRenderer(new DefaultListCellRenderer() {
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
        return selector;
    }

    private void mostrarHuespedes(List<Reserva> huespedes, LocalDate desde, LocalDate hasta) {
        // Presenta los resultados en una tabla de solo lectura con estilo uniforme.
        // Para hoteles agrego el tipo de habitacion y cuantas personas van.
        String[] columnas = {"Reserva", "Asistente", "Habitacion", "Personas", "Ingreso", "Egreso"};
        Object[][] filas = new Object[huespedes.size()][6];
        for (int i = 0; i < huespedes.size(); i++) {
            Reserva reserva = huespedes.get(i);
            String nombreAsistente = String.valueOf(
                    ventana.getGestorUsuarios().buscarPorId(reserva.getIdAsistente()).getNombre());
            String habitacion = reserva.getTipoHabitacion() == 0
                    ? "-" : reserva.getTipoHabitacion() + " personas";
            filas[i] = new Object[]{
                    reserva.getId(), nombreAsistente, habitacion,
                    reserva.getCantidadPersonas(), reserva.getIngreso(), reserva.getEgreso()
            };
        }

        JTable tabla = new JTable(UtilidadesInterfaz.modeloTabla(columnas, filas));
        tabla.setFont(tabla.getFont().deriveFont(Font.PLAIN, 14f));
        tabla.setRowHeight(28);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        tabla.getTableHeader().setBackground(AZUL_OSCURO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
        JScrollPane desplazamiento = UtilidadesInterfaz.tabla(tabla);
        desplazamiento.setPreferredSize(new Dimension(560, 220));
        desplazamiento.setBorder(BorderFactory.createLineBorder(BORDE));

        String periodo = desde.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale.forLanguageTag("es-AR")))
                + " al "
                + hasta.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR")));
        JOptionPane.showMessageDialog(this, desplazamiento,
                "Huespedes alojados: " + periodo, JOptionPane.PLAIN_MESSAGE);
    }
}
