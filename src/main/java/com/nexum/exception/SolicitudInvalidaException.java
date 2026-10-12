package com.nexum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 400 — La solicitud del cliente es inválida (regla de negocio no cubierta por Jakarta Validation). */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}

