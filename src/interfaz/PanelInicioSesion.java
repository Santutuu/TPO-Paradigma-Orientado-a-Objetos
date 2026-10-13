package interfaz;

import modelo.Administrador;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla inicial (sin sesion): permite iniciar sesion, registrarse, consultar
 * la agenda y los alojamientos de forma publica.
 */
public class PanelInicioSesion extends JPanel {

    private final VentanaPrincipal ventana;
    private final JTextField campoUsuario;
    private final JPasswordField campoClave;

    public PanelInicioSesion(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());

        add(UtilidadesInterfaz.titulo("Bienvenido a la visita del Papa Leon XIV"), BorderLayout.NORTH);

        campoUsuario = new JTextField(16);
        campoClave = new JPasswordField(16);

        JPanel form = UtilidadesInterfaz.formulario(
                UtilidadesInterfaz.fila("Nombre de usuario:", campoUsuario),
                UtilidadesInterfaz.fila("Clave:", campoClave)
        );

        JButton botonIngresar = UtilidadesInterfaz.boton("Iniciar sesion", e -> iniciarSesion());
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(UtilidadesInterfaz.panelBotones(botonIngresar), BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        JButton botonRegistroAsistente = UtilidadesInterfaz.boton("Registrarse como asistente",
                e -> ventana.mostrarRegistroAsistente());
        JButton botonRegistroAlojamiento = UtilidadesInterfaz.boton("Registrarse como alojamiento",
                e -> ventana.mostrarRegistroAlojamiento());
        JButton botonAgenda = UtilidadesInterfaz.boton("Ver agenda por fecha",
                e -> ventana.mostrarAgenda());
        JButton botonAlojamientos = UtilidadesInterfaz.boton("Ver alojamientos por ciudad",
                e -> ventana.mostrarBusquedaAlojamientos());
        JButton botonSalir = UtilidadesInterfaz.boton("Salir", e -> System.exit(0));

        add(UtilidadesInterfaz.panelBotones(botonRegistroAsistente, botonRegistroAlojamiento,
                botonAgenda, botonAlojamientos, botonSalir), BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            UtilidadesInterfaz.mostrarError(this, "Completa usuario y clave.");
            return;
        }

        Usuario cuenta = ventana.getGestorUsuarios().autenticar(usuario, clave);
        if (cuenta == null) {
            UtilidadesInterfaz.mostrarError(this, "Usuario o clave incorrectos.");
            return;
        }

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
