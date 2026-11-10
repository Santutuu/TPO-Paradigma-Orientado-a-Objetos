package persistencia;

import modelo.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersistenciaJSON {

    /** Archivos que la aplicacion necesita para arrancar. */
    private static final String[] ARCHIVOS_BASE =
            {"cuentas.json", "eventos.json", "reservas.json"};

    private final String directorio;

    public PersistenciaJSON() {
        this.directorio = "datos";
    }

    public PersistenciaJSON(String directorio) {
        this.directorio = directorio;
    }

    public List<String> archivosBaseFaltantes() {
        List<String> faltantes = new ArrayList<>();
        for (String nombre : ARCHIVOS_BASE) {
            if (!Files.exists(Paths.get(directorio, nombre))) {
                faltantes.add(nombre);
            }
        }
        return faltantes;
    }

    public Path carpetaDatos() {
        return Paths.get(directorio).toAbsolutePath();
    }

    public Set<Usuario> cargarCuentas() throws IOException {
        Object raiz = parsearArchivo("cuentas.json");
        if (!(raiz instanceof List)) {
            throw new IOException("Formato invalido en cuentas.json: se esperaba un arreglo.");
        }
        Set<Usuario> cuentas = new LinkedHashSet<>();
        for (Object elemento : (List<?>) raiz) {
            Usuario usuario = construirUsuario((Map<String, Object>) elemento);
            if (!cuentas.add(usuario)) {
                throw new IOException("Nombre de usuario duplicado en cuentas.json: "
                        + usuario.getNombreUsuario());
            }
        }
        return cuentas;
    }

    public void guardarCuentas(Collection<Usuario> cuentas) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int i = 0;
        for (Usuario usuario : cuentas) {
            sb.append(usuarioJson(usuario));
            if (i < cuentas.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
            i++;
        }
        sb.append("]\n");
        escribirArchivo("cuentas.json", sb.toString());
    }

    public List<Evento> cargarEventos() throws IOException {
        Object raiz = parsearArchivo("eventos.json");
        if (!(raiz instanceof List)) {
            throw new IOException("Formato invalido en eventos.json: se esperaba un arreglo.");
        }
        List<Evento> eventos = new ArrayList<>();
        for (Object elemento : (List<?>) raiz) {
            eventos.add(construirEvento((Map<String, Object>) elemento));
        }
        return eventos;
    }

    public void guardarEventos(List<Evento> eventos) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < eventos.size(); i++) {
            sb.append("  ").append(eventoJson(eventos.get(i)));
            if (i < eventos.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        escribirArchivo("eventos.json", sb.toString());
    }

    public List<Reserva> cargarReservas() throws IOException {
        Object raiz = parsearArchivo("reservas.json");
        if (!(raiz instanceof List)) {
            throw new IOException("Formato invalido en reservas.json: se esperaba un arreglo.");
        }
        List<Reserva> reservas = new ArrayList<>();
        for (Object elemento : (List<?>) raiz) {
            reservas.add(construirReserva((Map<String, Object>) elemento));
        }
        return reservas;
    }

    public void guardarReservas(List<Reserva> reservas) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            sb.append("  { \"id\": ").append(r.getId());
            sb.append(", \"idAsistente\": ").append(r.getIdAsistente());
            sb.append(", \"idAlojamiento\": ").append(r.getIdAlojamiento());
            sb.append(", \"tipoHabitacion\": ").append(r.getTipoHabitacion());
            sb.append(", \"cantidadPersonas\": ").append(r.getCantidadPersonas());
            sb.append(", \"precioPorNoche\": ").append(r.getPrecioPorNoche());
            sb.append(", \"ingreso\": ").append(jsonCadena(r.getIngreso().toString()));
            sb.append(", \"egreso\": ").append(jsonCadena(r.getEgreso().toString()));
            sb.append(", \"estado\": ").append(jsonCadena(r.getEstado().name()));
            sb.append(" }");
            if (i < reservas.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        escribirArchivo("reservas.json", sb.toString());
    }

    /**
     * Carga las notificaciones. Si el archivo todavia no existe, devuelve una
     * lista vacia para no impedir el arranque de la aplicacion.
     */
    public List<Notificacion> cargarNotificaciones() throws IOException {
        Path archivo = Paths.get(directorio, "notificaciones.json");
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        if (leerArchivo(archivo).trim().isEmpty()) {
            return new ArrayList<>();
        }
        Object raiz = parsearArchivo("notificaciones.json");
        if (!(raiz instanceof List)) {
            throw new IOException("Formato invalido en notificaciones.json: se esperaba un arreglo.");
        }
        List<Notificacion> notificaciones = new ArrayList<>();
        for (Object elemento : (List<?>) raiz) {
            notificaciones.add(construirNotificacion((Map<String, Object>) elemento));
        }
        return notificaciones;
    }

    public void guardarNotificaciones(List<Notificacion> notificaciones) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < notificaciones.size(); i++) {
            sb.append("  ").append(notificacionJson(notificaciones.get(i)));
            if (i < notificaciones.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        escribirArchivo("notificaciones.json", sb.toString());
    }

    private Usuario construirUsuario(Map<String, Object> m) throws IOException {
        String tipo = (String) m.get("tipo");
        int id = numero(m.get("id")).intValue();
        String nombreUsuario = (String) m.get("nombreUsuario");
        String clave = (String) m.get("clave");
        String nombre = (String) m.get("nombre");

        switch (tipo) {
            case "ADMINISTRADOR":
                return UsuarioFactory.crearAdministrador(id, nombreUsuario, clave, nombre);
            case "ASISTENTE": {
                TipoDocumento tipoDocumento = tipoDocumento(m);
                String dni = (String) m.get("dni");
                String origen = (String) m.get("origen");
                Asistente asistente = UsuarioFactory.crearAsistente(id, nombreUsuario, clave,
                        nombre, tipoDocumento, dni, origen);
                Object eventos = m.get("eventos");
                if (eventos instanceof List) {
                    for (Object idEvento : (List<?>) eventos) {
                        asistente.inscribir(numero(idEvento).intValue());
                    }
                }
                return asistente;
            }
            case "PARROQUIA":
                return UsuarioFactory.crearParroquia(id, nombreUsuario, clave, nombre,
                        ciudad(m), direccion(m), capacidad(m), texto(m.get("responsable")));
            case "HOTEL":
                return construirHotel(id, nombreUsuario, clave, nombre, m);
            case "ESPACIO_ORG":
                return UsuarioFactory.crearEspacioOrg(id, nombreUsuario, clave, nombre,
                        ciudad(m), direccion(m), capacidad(m),
                        (String) m.get("organizador"));
            default:
                throw new IOException("Tipo de cuenta desconocido en cuentas.json: " + tipo);
        }
    }

    private Hotel construirHotel(int id, String nombreUsuario, String clave, String nombre,
                                 Map<String, Object> m) throws IOException {
        int categoria = numero(m.get("categoria")).intValue();
        Object precios = m.get("preciosPorTipo");
        double precioSimple;
        double precioDoble;
        double precioTriple;
        double precioCuadruple;
        if (precios instanceof Map) {
            Map<String, Object> detalle = (Map<String, Object>) precios;
            precioSimple = precioHabitacion(detalle, "1");
            precioDoble = precioHabitacion(detalle, "2");
            precioTriple = precioHabitacion(detalle, "3");
            precioCuadruple = precioHabitacion(detalle, "4");
        } else {
            Object precio = m.get("precioPorNoche");
            double unico = precio == null ? 0.0 : numero(precio).doubleValue();
            precioSimple = unico;
            precioDoble = unico;
            precioTriple = unico;
            precioCuadruple = unico;
        }
        Object habitaciones = m.get("habitaciones");

        int simples;
        int dobles;
        int triples;
        int cuadruples;
        if (habitaciones instanceof Map) {
            Map<String, Object> detalle = (Map<String, Object>) habitaciones;
            simples = cantidadHabitaciones(detalle, "1");
            dobles = cantidadHabitaciones(detalle, "2");
            triples = cantidadHabitaciones(detalle, "3");
            cuadruples = cantidadHabitaciones(detalle, "4");
        } else {
            simples = capacidad(m);
            dobles = 0;
            triples = 0;
            cuadruples = 0;
        }

        return UsuarioFactory.crearHotel(id, nombreUsuario, clave, nombre, ciudad(m),
                direccion(m), categoria, precioSimple, precioDoble, precioTriple,
                precioCuadruple, simples, dobles, triples, cuadruples);
    }

    private int cantidadHabitaciones(Map<String, Object> detalle, String personas) throws IOException {
        Object valor = detalle.get(personas);
        return valor == null ? 0 : numero(valor).intValue();
    }

    private double precioHabitacion(Map<String, Object> detalle, String personas) throws IOException {
        Object valor = detalle.get(personas);
        return valor == null ? 0.0 : numero(valor).doubleValue();
    }

    private TipoDocumento tipoDocumento(Map<String, Object> m) throws IOException {
        Object valor = m.get("tipoDocumento");
        if (valor == null) {
            return TipoDocumento.DNI;
        }
        try {
            return TipoDocumento.valueOf((String) valor);
        } catch (IllegalArgumentException e) {
            throw new IOException("Tipo de documento desconocido en cuentas.json: " + valor);
        }
    }

    private Evento construirEvento(Map<String, Object> m) throws IOException {
        int id = numero(m.get("id")).intValue();
        LocalTime horario = LocalTime.parse((String) m.get("horario"));
        String direccion = (String) m.get("direccion");
        if (direccion == null || direccion.trim().isEmpty()) {
            direccion = "A confirmar";
        }
        EstadoEvento estado = EstadoEvento.ACTIVA;
        Object valorEstado = m.get("estado");
        if (valorEstado != null) {
            try {
                estado = EstadoEvento.valueOf((String) valorEstado);
            } catch (IllegalArgumentException e) {
                throw new IOException("Estado de evento desconocido en eventos.json: " + valorEstado);
            }
        }
        return new Evento(id,
                LocalDate.parse((String) m.get("fecha")),
                horario,
                horarioFin(m, horario),
                (String) m.get("actividad"),
                Ciudad.valueOf((String) m.get("ciudad")),
                direccion,
                estado);
    }

    private Reserva construirReserva(Map<String, Object> m) throws IOException {
        int id = numero(m.get("id")).intValue();
        int idAsistente = numero(m.get("idAsistente")).intValue();
        int idAlojamiento = numero(m.get("idAlojamiento")).intValue();
        int tipoHabitacion = m.get("tipoHabitacion") == null
                ? 0 : numero(m.get("tipoHabitacion")).intValue();
        int cantidadPersonas = m.get("cantidadPersonas") == null
                ? 1 : numero(m.get("cantidadPersonas")).intValue();
        double precioPorNoche = m.get("precioPorNoche") == null
                ? 0.0 : numero(m.get("precioPorNoche")).doubleValue();
        LocalDate ingreso = LocalDate.parse((String) m.get("ingreso"));
        LocalDate egreso = LocalDate.parse((String) m.get("egreso"));
        EstadoReserva estado = EstadoReserva.valueOf((String) m.get("estado"));
        return new Reserva(id, idAsistente, idAlojamiento, tipoHabitacion,
                cantidadPersonas, precioPorNoche, ingreso, egreso, estado);
    }

    private Notificacion construirNotificacion(Map<String, Object> m) throws IOException {
        int id = numero(m.get("id")).intValue();
        int idDestinatario = numero(m.get("idDestinatario")).intValue();
        LocalDateTime fechaHora = LocalDateTime.parse((String) m.get("fechaHora"));
        String tipo = (String) m.get("tipo");
        String mensaje = (String) m.get("mensaje");
        boolean leida = Boolean.TRUE.equals(m.get("leida"));
        return new Notificacion(id, idDestinatario, fechaHora, tipo, mensaje, leida);
    }

    /** Lee el horario de fin; si el archivo es viejo, estima una hora de duracion. */
    private LocalTime horarioFin(Map<String, Object> m, LocalTime horario) {
        Object valor = m.get("horarioFin");
        return valor == null ? horario.plusHours(1) : LocalTime.parse((String) valor);
    }

    private String usuarioJson(Usuario u) {
        StringBuilder sb = new StringBuilder();
        sb.append("  { ");
        sb.append("\"tipo\": ").append(jsonCadena(tipoUsuario(u)));
        sb.append(", \"id\": ").append(u.getId());
        sb.append(", \"nombreUsuario\": ").append(jsonCadena(u.getNombreUsuario()));
        sb.append(", \"clave\": ").append(jsonCadena(u.getClave()));
        sb.append(", \"nombre\": ").append(jsonCadena(u.getNombre()));

        if (u instanceof Asistente) {
            Asistente a = (Asistente) u;
            sb.append(", \"tipoDocumento\": ").append(jsonCadena(a.getTipoDocumento().name()));
            sb.append(", \"dni\": ").append(jsonCadena(a.getDni()));
            sb.append(", \"origen\": ").append(jsonCadena(a.getOrigen()));
            sb.append(", \"eventos\": [");
            int i = 0;
            for (int idEvento : a.getEventos()) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(idEvento);
                i++;
            }
            sb.append("]");
        } else if (u instanceof Alojamiento) {
            Alojamiento al = (Alojamiento) u;
            sb.append(", \"ciudad\": ").append(jsonCadena(al.getCiudad().name()));
            sb.append(", \"direccion\": ").append(jsonCadena(al.getDireccion()));
            sb.append(", \"capacidad\": ").append(al.getCapacidad());
            if (al instanceof Parroquia) {
                sb.append(", \"responsable\": ").append(jsonCadena(((Parroquia) al).getResponsable()));
            } else if (al instanceof Hotel) {
                Hotel hotel = (Hotel) al;
                sb.append(", \"categoria\": ").append(hotel.getCategoria());
                sb.append(", \"preciosPorTipo\": {");
                int j = 0;
                for (Map.Entry<Integer, Double> entrada
                        : hotel.getPreciosPorTipoHabitacion().entrySet()) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    sb.append("\"").append(entrada.getKey()).append("\": ")
                            .append(entrada.getValue());
                    j++;
                }
                sb.append(" }");
                sb.append(", \"habitaciones\": {");
                int i = 0;
                for (Map.Entry<Integer, Integer> entrada
                        : hotel.getHabitacionesPorPersonas().entrySet()) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append("\"").append(entrada.getKey()).append("\": ")
                            .append(entrada.getValue());
                    i++;
                }
                sb.append(" }");
            } else if (al instanceof EspacioOrg) {
                sb.append(", \"organizador\": ").append(jsonCadena(((EspacioOrg) al).getOrganizador()));
            }
        }
        sb.append(" }");
        return sb.toString();
    }

    private String eventoJson(Evento e) {
        return "{ \"id\": " + e.getId()
                + ", \"fecha\": " + jsonCadena(e.getFecha().toString())
                + ", \"horario\": " + jsonCadena(e.getHorario().toString())
                + ", \"horarioFin\": " + jsonCadena(e.getHorarioFin().toString())
                + ", \"actividad\": " + jsonCadena(e.getActividad())
                + ", \"ciudad\": " + jsonCadena(e.getCiudad().name())
                + ", \"direccion\": " + jsonCadena(e.getDireccion())
                + ", \"estado\": " + jsonCadena(e.getEstado().name()) + " }";
    }

    private String notificacionJson(Notificacion n) {
        return "{ \"id\": " + n.getId()
                + ", \"idDestinatario\": " + n.getIdDestinatario()
                + ", \"fechaHora\": " + jsonCadena(n.getFechaHora().toString())
                + ", \"tipo\": " + jsonCadena(n.getTipo())
                + ", \"mensaje\": " + jsonCadena(n.getMensaje())
                + ", \"leida\": " + n.isLeida() + " }";
    }

    private String tipoUsuario(Usuario u) {
        return u.getTipoCuenta();
    }

    private Ciudad ciudad(Map<String, Object> m) {
        return Ciudad.valueOf((String) m.get("ciudad"));
    }

    private String direccion(Map<String, Object> m) {
        return (String) m.get("direccion");
    }

    private int capacidad(Map<String, Object> m) throws IOException {
        return numero(m.get("capacidad")).intValue();
    }

    /** Devuelve el texto indicado o una cadena vacia si el campo no existe. */
    private String texto(Object valor) {
        return valor == null ? "" : (String) valor;
    }

    private Number numero(Object valor) throws IOException {
        if (valor instanceof Number) {
            return (Number) valor;
        }
        throw new IOException("Se esperaba un numero en el archivo JSON pero se encontro: " + valor);
    }

    private Object parsearArchivo(String nombre) throws IOException {
        Path archivo = Paths.get(directorio, nombre);
        if (!Files.exists(archivo)) {
            throw new IOException("No se encontro el archivo " + archivo.toAbsolutePath() + ".");
        }
        String contenido = leerArchivo(archivo).trim();
        if (contenido.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return new ParserJSON().parsear(contenido);
        } catch (IllegalArgumentException e) {
            throw new IOException("Formato JSON invalido en " + nombre + ": " + e.getMessage());
        }
    }

    /** Lee un archivo completo con BufferedReader en UTF-8. */
    private String leerArchivo(Path archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(linea);
            }
        }
        return sb.toString();
    }

    /** Escribe un archivo completo con BufferedWriter en UTF-8. */
    private synchronized void escribirArchivo(String nombre, String contenido) throws IOException {
        Path carpeta = Paths.get(directorio);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
        Path archivo = Paths.get(directorio, nombre);
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write(contenido);
        }
    }

    private String jsonCadena(String texto) {
        StringBuilder sb = new StringBuilder("\"");
        if (texto != null) {
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                switch (c) {
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    default:
                        if (c < 0x20) {
                            sb.append(String.format("\\u%04x", (int) c));
                        } else {
                            sb.append(c);
                        }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
