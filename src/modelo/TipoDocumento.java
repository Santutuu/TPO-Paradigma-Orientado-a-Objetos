package modelo;

/**
 * Tipo de documento de identidad del asistente. Permite distinguir un DNI de
 * un pasaporte u otro documento, de modo que dos personas puedan tener el
 * mismo numero sin que el sistema las confunda.
 */
public enum TipoDocumento {
    DNI,
    PASAPORTE,
    OTRO
}
