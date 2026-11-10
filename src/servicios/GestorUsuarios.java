package servicios;

import modelo.*;
import persistencia.ErrorBaseDatos;
import persistencia.PersistenciaJSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Gestiona el registro, la autenticacion y la busqueda de cuentas. Cada cuenta
 * se identifica por un id unico y por su nombre de usuario.
 */
public class GestorUsuarios {

    private final Set<Usuario> usuarios;
    private final PersistenciaJSON persistencia;

    public GestorUsuarios(Set<Usuario> usuarios, PersistenciaJSON persistencia) {
        this.usuarios = usuarios;
        this.persistencia = persistencia;
    }

    public synchronized Set<Usuario> getUsuarios() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(usuarios));
    }

    public synchronized boolean existeNombreUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Usuario autenticar(String nombreUsuario, String clave) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario) && u.validarClave(clave)) {
                return u;
            }
        }
        return null;
    }

    public synchronized Asistente registrarAsistente(Asistente asistente) {
        if (existeNombreUsuario(asistente.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso.");
        }
        if (existeDocumento(asistente.getTipoDocumento(), asistente.getDni())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese tipo y numero de documento.");
        }
        asistente.setId(proximoId());
        if (!usuarios.add(asistente)) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso.");
        }
        guardarCuentas();
        return asistente;
    }

    public synchronized Alojamiento registrarAlojamiento(Alojamiento alojamiento) {
        if (existeNombreUsuario(alojamiento.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso.");
        }
        alojamiento.setId(proximoId());
        if (!usuarios.add(alojamiento)) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso.");
        }
        guardarCuentas();
        return alojamiento;
    }

    public synchronized boolean existeDocumento(TipoDocumento tipoDocumento, String numero) {
        String buscado = normalizarDocumento(numero);
        if (buscado.isEmpty()) {
            return false;
        }
        for (Usuario u : usuarios) {
            if (u.esAsistente()) {
                Asistente a = (Asistente) u;
                if (a.getTipoDocumento() == tipoDocumento
                        && normalizarDocumento(a.getDni()).equals(buscado)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String normalizarDocumento(String numero) {
        if (numero == null) {
            return "";
        }
        return numero.replaceAll("[^0-9A-Za-z]", "").toUpperCase(Locale.ROOT);
    }

    public synchronized Usuario buscarPorId(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public synchronized Asistente buscarAsistente(int id) {
        Usuario u = buscarPorId(id);
        return (u != null && u.esAsistente()) ? (Asistente) u : null;
    }

    public synchronized Alojamiento buscarAlojamiento(int id) {
        Usuario u = buscarPorId(id);
        return (u != null && u.esAlojamiento()) ? (Alojamiento) u : null;
    }

    public synchronized List<Alojamiento> listarAlojamientos() {
        List<Alojamiento> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.esAlojamiento()) {
                resultado.add((Alojamiento) u);
            }
        }
        return resultado;
    }

    public synchronized List<Alojamiento> listarAlojamientosPorCiudad(Ciudad ciudad) {
        List<Alojamiento> resultado = new ArrayList<>();
        for (Alojamiento a : listarAlojamientos()) {
            if (a.getCiudad() == ciudad) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    private synchronized int proximoId() {
        int maximo = 0;
        for (Usuario u : usuarios) {
            if (u.getId() > maximo) {
                maximo = u.getId();
            }
        }
        return maximo + 1;
    }

    private void guardarCuentas() {
        try {
            persistencia.guardarCuentas(usuarios);
        } catch (IOException e) {
            throw new ErrorBaseDatos("No se pudo guardar cuentas.json: " + e.getMessage(), e);
        }
    }
}
