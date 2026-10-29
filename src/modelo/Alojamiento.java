package modelo;

/**
 * Alojamiento que ofrece hospedaje durante la visita. Es abstracto porque cada
 * tipo concreto (parroquia, hotel, espacio de organizacion) define su propia
 * descripcion.
 */
public abstract class Alojamiento extends Usuario {

    private Ciudad ciudad;
    private String direccion;
    private int capacidad;

    public Alojamiento(int id, String nombreUsuario, String clave, String nombre,
                       Ciudad ciudad, String direccion, int capacidad) {
        super(id, nombreUsuario, clave, nombre);
        this.ciudad = ciudad;
        this.direccion = direccion;
        setCapacidad(capacidad);
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    /**
     * La capacidad debe ser un entero positivo.
     */
    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un numero positivo.");
        }
        this.capacidad = capacidad;
    }

    /**
     * Descripcion util y legible del alojamiento para mostrar en la interfaz.
     */
    public abstract String descripcion();

    @Override
    public String toString() {
        return getNombre() + " (" + getCiudad() + ")";
    }

    @Override
    public String getRol() {
        return "ALOJAMIENTO";
    }
}
