package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Asistente a la visita del Papa. Guarda la lista de eventos a los que esta
 * inscrito como una lista de identificadores (no guarda copias de los eventos).
 */
public class Asistente extends Usuario {

    private String dni;
    private Ciudad origen;
    private List<Integer> eventos;

    public Asistente(int id, String nombreUsuario, String clave, String nombre,
                     String dni, Ciudad origen) {
        super(id, nombreUsuario, clave, nombre);
        this.dni = dni;
        this.origen = origen;
        this.eventos = new ArrayList<>();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Ciudad getOrigen() {
        return origen;
    }

    public void setOrigen(Ciudad origen) {
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

    public boolean estaInscrito(int idEvento) {
        return eventos.contains(idEvento);
    }

    @Override
    public String getRol() {
        return "ASISTENTE";
    }
}
