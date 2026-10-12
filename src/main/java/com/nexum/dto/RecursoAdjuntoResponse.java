package com.nexum.dto;

import com.nexum.entity.RecursoAdjunto;

import java.util.UUID;

public record RecursoAdjuntoResponse(
        UUID id,
        UUID leccionId,
        String nombre,
        String urlArchivo,
        String tipoArchivo
) {
    public static RecursoAdjuntoResponse desde(RecursoAdjunto recurso) {
        return new RecursoAdjuntoResponse(
                recurso.getId(),
                recurso.getLeccion() != null ? recurso.getLeccion().getId() : null,
                recurso.getNombre(),
                recurso.getUrlArchivo(),
                recurso.getTipoArchivo()
        );
    }
}
