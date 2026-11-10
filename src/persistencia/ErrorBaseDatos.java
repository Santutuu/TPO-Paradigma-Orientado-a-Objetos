package persistencia;

public class ErrorBaseDatos extends RuntimeException {

    public ErrorBaseDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
