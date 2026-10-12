package com.nexum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 409 — Conflicto de integridad (email duplicado, slug duplicado, dependientes, etc.). */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}

