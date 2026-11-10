package modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


public class Asistente extends Usuario {

    private TipoDocumento tipoDocumento;
    private String dni;
    private String origen;
    private final Set<Integer> eventos;

    public Asistente(int id, String nombreUsuario, String clave, String nombre,
                     TipoDocumento tipoDocumento, String dni, String origen) {
        super(id, nombreUsuario, clave, nombre);
        setTipoDocumento(tipoDocumento);
        setDni(dni);
        this.origen = origen;
        this.eventos = new LinkedHashSet<>();
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

    public synchronized Set<Integer> getEventos() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(eventos));
    }

    /**
     * Inscribe al asistente en un evento. No permite inscripciones duplicadas.
     */
    public synchronized void inscribir(int idEvento) {
        eventos.add(idEvento);
    }

    /**
     * Quita al asistente de un evento.
     */
    public synchronized void desinscribir(int idEvento) {
        eventos.remove(Integer.valueOf(idEvento));
    }

    /**
     * Indica si el asistente ya esta inscripto en el evento indicado.
     */
    public synchronized boolean estaInscrito(int idEvento) {
        return eventos.contains(idEvento);
    }

    @Override
    public String getTipoCuenta() {
        return "ASISTENTE";
    }

    @Override
    public boolean esAsistente() {
        return true;
    }

    @Override
    public String getRol() {
        return "ASISTENTE";
    }
}
