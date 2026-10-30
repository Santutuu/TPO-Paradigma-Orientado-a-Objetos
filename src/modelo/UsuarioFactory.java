package modelo;

/**
 * Fabrica simple de cuentas. Centraliza la construccion de cada subtipo de
 * {@link Usuario} para que la persistencia y la interfaz no repitan los
 * constructores concretos.
 *
 * La seleccion del tipo se hace afuera (en {@code PersistenciaJSON} al leer el
 * campo {@code tipo}, o en los formularios de registro); esta clase se encarga
 * de construir el objeto correcto y de que las validaciones del modelo se
 * apliquen siempre por el mismo camino.
 */
public final class UsuarioFactory {

    private UsuarioFactory() {
        // No se instancia: solo ofrece metodos de creacion.
    }

    public static Administrador crearAdministrador(int id, String nombreUsuario,
                                                   String clave, String nombre) {
        return new Administrador(id, nombreUsuario, clave, nombre);
    }

    public static Asistente crearAsistente(int id, String nombreUsuario, String clave,
                                           String nombre, TipoDocumento tipoDocumento,
                                           String dni, String origen) {
        return new Asistente(id, nombreUsuario, clave, nombre, tipoDocumento, dni, origen);
    }

    public static Parroquia crearParroquia(int id, String nombreUsuario, String clave,
                                           String nombre, Ciudad ciudad, String direccion,
                                           int capacidad, String responsable) {
        return new Parroquia(id, nombreUsuario, clave, nombre, ciudad, direccion,
                capacidad, responsable);
    }

    public static Hotel crearHotel(int id, String nombreUsuario, String clave, String nombre,
                                   Ciudad ciudad, String direccion, int categoria,
                                   double precioSimple, double precioDoble,
                                   double precioTriple, double precioCuadruple,
                                   int habitacionesSimples, int habitacionesDobles,
                                   int habitacionesTriples, int habitacionesCuadruples) {
        return new Hotel(id, nombreUsuario, clave, nombre, ciudad, direccion, categoria,
                precioSimple, precioDoble, precioTriple, precioCuadruple,
                habitacionesSimples, habitacionesDobles, habitacionesTriples,
                habitacionesCuadruples);
    }

    public static EspacioOrg crearEspacioOrg(int id, String nombreUsuario, String clave,
                                             String nombre, Ciudad ciudad, String direccion,
                                             int capacidad, String organizador) {
        return new EspacioOrg(id, nombreUsuario, clave, nombre, ciudad, direccion,
                capacidad, organizador);
    }
}
