package modelo;

import java.util.Locale;

public abstract class Usuario {

    private int id;
    private String nombreUsuario;
    private String clave;
    private String nombre;

    public Usuario(int id, String nombreUsuario, String clave, String nombre) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean validarClave(String claveIngresada) {
        return this.clave.equals(claveIngresada);
    }

    /**
     * Identidad del usuario para colecciones: el nombre de usuario es unico sin
     * distinguir mayusculas ni espacios externos.
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Usuario)) {
            return false;
        }
        Usuario usuario = (Usuario) otro;
        return nombreUsuarioNormalizado().equals(usuario.nombreUsuarioNormalizado());
    }

    @Override
    public int hashCode() {
        return nombreUsuarioNormalizado().hashCode();
    }

    private String nombreUsuarioNormalizado() {
        return nombreUsuario == null ? "" : nombreUsuario.trim().toLowerCase(Locale.ROOT);
    }

    /** Tipo concreto de cuenta, usado por la persistencia. */
    public abstract String getTipoCuenta();

    public boolean esAsistente() {
        return false;
    }

    public boolean esAlojamiento() {
        return false;
    }

    public boolean esAdministrador() {
        return false;
    }

    public abstract String getRol();
}
