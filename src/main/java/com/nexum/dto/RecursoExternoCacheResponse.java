package com.nexum.dto;

import com.nexum.entity.RecursoExternoCache;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record RecursoExternoCacheResponse(
        UUID id,
        UUID leccionId,
        String tipo,
        String titulo,
        String descripcion,
        String url,
        String thumbnail,
        String fuente,
        Map<String, Object> metadata,
        LocalDateTime fechaCreacion
) {
    public static RecursoExternoCacheResponse desde(RecursoExternoCache recurso) {
        return new RecursoExternoCacheResponse(
                recurso.getId(),
                recurso.getLeccion() != null ? recurso.getLeccion().getId() : null,
                recurso.getTipo(),
                recurso.getTitulo(),
                recurso.getDescripcion(),
                recurso.getUrl(),
                recurso.getThumbnail(),
                recurso.getFuente(),
                recurso.getMetadata(),
                recurso.getFechaCreacion()
        );
    }
}
