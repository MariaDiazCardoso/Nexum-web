package com.nexum.dto;

import com.nexum.entity.Leccion;

import java.util.UUID;

public record LeccionResponse(
        UUID id,
        UUID moduloId,
        String titulo,
        String tipo,
        Integer orden,
        String contenidoTexto,
        Integer puntosOtorgados,
        String videoUrl,
        Integer xpOtorgada,
        String slug,
        String retoPractico,
        String dificultad,
        String duracionEstimada,
        Integer duracionMinima,
        String imagenUrl
) {
    public static LeccionResponse desde(Leccion leccion) {
        return new LeccionResponse(
                leccion.getId(),
                leccion.getModulo() != null ? leccion.getModulo().getId() : null,
                leccion.getTitulo(),
                leccion.getTipo(),
                leccion.getOrden(),
                leccion.getContenidoTexto(),
                leccion.getPuntosOtorgados(),
                leccion.getVideoUrl(),
                leccion.getXpOtorgada(),
                leccion.getSlug(),
                leccion.getRetoPractico(),
                leccion.getDificultad(),
                leccion.getDuracionEstimada(),
                leccion.getDuracionMinima(),
                leccion.getImagenUrl()
        );
    }
}
