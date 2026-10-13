package modelo;

/**
 * Alojamiento de tipo parroquia.
 */
public class Parroquia extends Alojamiento {

    public Parroquia(int id, String nombreUsuario, String clave, String nombre,
                     Ciudad ciudad, String direccion, int capacidad) {
        super(id, nombreUsuario, clave, nombre, ciudad, direccion, capacidad);
    }

    @Override
    public String descripcion() {
        return "Parroquia " + getNombre() + " - " + getDireccion() + ", "
                + getCiudad() + " (capacidad: " + getCapacidad() + ")";
    }
}
