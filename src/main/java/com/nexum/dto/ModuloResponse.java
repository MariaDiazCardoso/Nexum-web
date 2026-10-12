package com.nexum.dto;

import com.nexum.entity.Modulo;

import java.util.UUID;

public record ModuloResponse(
        UUID id,
        UUID cursoId,
        String titulo,
        Integer orden,
        String descripcion,
        String slug
) {
    public static ModuloResponse desde(Modulo modulo) {
        return new ModuloResponse(
                modulo.getId(),
                modulo.getCurso() != null ? modulo.getCurso().getId() : null,
                modulo.getTitulo(),
                modulo.getOrden(),
                modulo.getDescripcion(),
                modulo.getSlug()
        );
    }
}
