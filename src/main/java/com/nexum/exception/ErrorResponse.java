package com.nexum.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cuerpo uniforme para todas las respuestas de error de la API.
 * El campo {@code errores} solo se incluye en errores de validación de campos.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoError> errores
) {

    /** Par campo → mensaje de validación. */
    public record CampoError(String campo, String mensaje) {}

    // Constructor sin errores de campo (uso general)
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    // Constructor con errores de campo (validación Jakarta)
    public static ErrorResponse ofValidacion(int status, String error, String message,
                                             String path, List<CampoError> errores) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, errores);
    }
}

