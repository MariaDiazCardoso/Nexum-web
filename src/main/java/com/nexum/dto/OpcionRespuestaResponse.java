package com.nexum.dto;

import com.nexum.entity.OpcionRespuesta;

import java.util.UUID;

public record OpcionRespuestaResponse(
        UUID id,
        String textoOpcion,
        Boolean esCorrecta
) {
    public static OpcionRespuestaResponse desde(OpcionRespuesta opcion) {
        return new OpcionRespuestaResponse(
                opcion.getId(),
                opcion.getTextoOpcion(),
                opcion.getEsCorrecta()
        );
    }

    public static OpcionRespuestaResponse desde(OpcionRespuesta opcion, boolean mostrarCorrecta) {
        return new OpcionRespuestaResponse(
                opcion.getId(),
                opcion.getTextoOpcion(),
                mostrarCorrecta ? opcion.getEsCorrecta() : null
        );
    }
}
