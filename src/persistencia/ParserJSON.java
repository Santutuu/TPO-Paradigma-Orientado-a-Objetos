package persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser de JSON sencillo, escrito a mano, que soporta el subconjunto de JSON
 * que usa la aplicacion: objetos, arreglos, cadenas, numeros, booleanos y null.
 * Rechaza entradas invalidas lanzando una excepcion.
 */
public class ParserJSON {

    private String texto;
    private int posicion;
    private int longitud;

    /**
     * Parsea un texto JSON y devuelve un Object, List o Map segun corresponda.
     */
    public Object parsear(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto a parsear no puede ser null.");
        }
        this.texto = texto;
        this.posicion = 0;
        this.longitud = texto.length();

        Object resultado = leerValor();
        saltarEspacios();
        if (posicion != longitud) {
            throw error("caracteres sobrantes al final del texto");
        }
        return resultado;
    }

    private void saltarEspacios() {
        while (posicion < longitud && Character.isWhitespace(texto.charAt(posicion))) {
            posicion++;
        }
    }

    private Object leerValor() {
        saltarEspacios();
        if (posicion >= longitud) {
            throw error("se esperaba un valor");
        }
        char c = texto.charAt(posicion);
        switch (c) {
            case '{':
                return leerObjeto();
            case '[':
                return leerArreglo();
            case '"':
                return leerCadena();
            case 't':
            case 'f':
                return leerBooleano();
            case 'n':
                return leerNulo();
            default:
                return leerNumero();
        }
    }

    private Map<String, Object> leerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        posicion++; // consume '{'
        saltarEspacios();
        if (posicion < longitud && texto.charAt(posicion) == '}') {
            posicion++;
            return mapa;
        }
        while (true) {
            saltarEspacios();
            if (posicion >= longitud || texto.charAt(posicion) != '"') {
                throw error("se esperaba una clave de tipo cadena");
            }
            String clave = leerCadena();
            saltarEspacios();
            if (posicion >= longitud || texto.charAt(posicion) != ':') {
                throw error("se esperaba ':' despues de la clave");
            }
            posicion++;
            Object valor = leerValor();
            mapa.put(clave, valor);
            saltarEspacios();
            if (posicion < longitud && texto.charAt(posicion) == ',') {
                posicion++;
                continue;
            }
            if (posicion < longitud && texto.charAt(posicion) == '}') {
                posicion++;
                return mapa;
            }
            throw error("se esperaba ',' o '}'");
        }
    }

    private List<Object> leerArreglo() {
        List<Object> lista = new ArrayList<>();
        posicion++; // consume '['
        saltarEspacios();
        if (posicion < longitud && texto.charAt(posicion) == ']') {
            posicion++;
            return lista;
        }
        while (true) {
            Object valor = leerValor();
            lista.add(valor);
            saltarEspacios();
            if (posicion < longitud && texto.charAt(posicion) == ',') {
                posicion++;
                continue;
            }
            if (posicion < longitud && texto.charAt(posicion) == ']') {
                posicion++;
                return lista;
            }
            throw error("se esperaba ',' o ']'");
        }
    }

    private String leerCadena() {
        posicion++; // consume '"'
        StringBuilder sb = new StringBuilder();
        while (posicion < longitud) {
            char c = texto.charAt(posicion);
            if (c == '"') {
                posicion++;
                return sb.toString();
            }
            if (c == '\\') {
                posicion++;
                if (posicion >= longitud) {
                    throw error("secuencia de escape incompleta");
                }
                char escape = texto.charAt(posicion);
                switch (escape) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'u':
                        if (posicion + 4 >= longitud) {
                            throw error("escape unicode incompleto");
                        }
                        String hex = texto.substring(posicion + 1, posicion + 5);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw error("escape unicode invalido");
                        }
                        posicion += 4;
                        break;
                    default:
                        throw error("escape desconocido: \\" + escape);
                }
                posicion++;
            } else {
                sb.append(c);
                posicion++;
            }
        }
        throw error("cadena sin cerrar");
    }

    private Object leerBooleano() {
        if (texto.startsWith("true", posicion)) {
            posicion += 4;
            return Boolean.TRUE;
        }
        if (texto.startsWith("false", posicion)) {
            posicion += 5;
            return Boolean.FALSE;
        }
        throw error("valor booleano invalido");
    }

    private Object leerNulo() {
        if (texto.startsWith("null", posicion)) {
            posicion += 4;
            return null;
        }
        throw error("valor nulo invalido");
    }

    private Object leerNumero() {
        int inicio = posicion;
        while (posicion < longitud) {
            char c = texto.charAt(posicion);
            if (c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E'
                    || Character.isDigit(c)) {
                posicion++;
            } else {
                break;
            }
        }
        String numero = texto.substring(inicio, posicion);
        if (numero.isEmpty()) {
            throw error("numero invalido");
        }
        try {
            if (numero.contains(".") || numero.contains("e") || numero.contains("E")) {
                return Double.parseDouble(numero);
            }
            return Long.parseLong(numero);
        } catch (NumberFormatException e) {
            throw error("numero invalido: " + numero);
        }
    }

    private IllegalArgumentException error(String mensaje) {
        return new IllegalArgumentException("JSON invalido (posicion " + posicion + "): " + mensaje);
    }
}
