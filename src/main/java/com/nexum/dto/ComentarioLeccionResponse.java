package com.nexum.dto;

import com.nexum.entity.ComentarioLeccion;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComentarioLeccionResponse(
        UUID id,
        UUID usuarioId,
        UUID leccionId,
        UUID comentarioPadreId,
        String contenido,
        LocalDateTime fecha
) {
    public static ComentarioLeccionResponse desde(ComentarioLeccion comentario) {
        return new ComentarioLeccionResponse(
                comentario.getId(),
                comentario.getUsuario() != null ? comentario.getUsuario().getId() : null,
                comentario.getLeccion() != null ? comentario.getLeccion().getId() : null,
                comentario.getComentarioPadre() != null ? comentario.getComentarioPadre().getId() : null,
                comentario.getContenido(),
                comentario.getFecha()
        );
    }
}
