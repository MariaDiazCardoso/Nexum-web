package com.nexum.dto;

import com.nexum.entity.Categoria;

import java.util.UUID;

/** DTO de respuesta para Categoria. */
public record CategoriaResponse(
        UUID id,
        String nombre,
        String iconoUrl
) {
    public static CategoriaResponse desde(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getIconoUrl());
    }
}

