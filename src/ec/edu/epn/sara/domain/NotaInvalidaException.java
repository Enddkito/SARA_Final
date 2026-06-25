package ec.edu.epn.sara.domain;

/**
 * Excepción personalizada para controlar el ingreso de calificaciones fuera del rango legal [0, 10].
 */
public class NotaInvalidaException extends Exception {

    public NotaInvalidaException(double notaIngresada) {
        super("Error de consistencia académica: La calificación ingresada (" + notaIngresada + ") no pertenece al rango válido [0.0 - 10.0].");
    }
}