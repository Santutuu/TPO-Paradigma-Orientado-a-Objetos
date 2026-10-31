package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Asistente a la visita del Papa. Guarda la lista de eventos a los que esta
 * inscripto como identificadores, tal como indica el UML.
 */
public class Asistente extends Usuario {

    // Tipo y numero de documento sirven para no duplicar personas.
    private TipoDocumento tipoDocumento;
    private String dni;
    // Pais de origen del asistente.
    private String origen;
    // Ids de los eventos a los que esta inscripto (no guardo los objetos).
    private List<Integer> eventos;

    public Asistente(int id, String nombreUsuario, String clave, String nombre,
                     TipoDocumento tipoDocumento, String dni, String origen) {
        super(id, nombreUsuario, clave, nombre);
        setTipoDocumento(tipoDocumento);
        setDni(dni);
        this.origen = origen;
        this.eventos = new ArrayList<>();
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }
        this.tipoDocumento = tipoDocumento;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni == null ? "" : dni.trim();
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public List<Integer> getEventos() {
        return eventos;
    }

    /**
     * Inscribe al asistente en un evento. No permite inscripciones duplicadas.
     */
    public void inscribir(int idEvento) {
        if (!eventos.contains(idEvento)) {
            eventos.add(idEvento);
        }
    }

    /**
     * Quita al asistente de un evento.
     */
    public void desinscribir(int idEvento) {
        eventos.remove(Integer.valueOf(idEvento));
    }

    /**
     * Indica si el asistente ya esta inscripto en el evento indicado.
     */
    public boolean estaInscrito(int idEvento) {
        return eventos.contains(idEvento);
    }

    @Override
    public String getRol() {
        return "ASISTENTE";
    }
}
