package com.nexum.dto;

import com.nexum.entity.UsuarioMedalla;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioMedallaResponse(
        UUID usuarioId,
        UUID medallaId,
        String nombre,
        String descripcion,
        String imagenUrl,
        LocalDateTime fechaObtencion
) {
    public static UsuarioMedallaResponse desde(UsuarioMedalla usuarioMedalla) {
        return new UsuarioMedallaResponse(
                usuarioMedalla.getUsuario() != null ? usuarioMedalla.getUsuario().getId() : null,
                usuarioMedalla.getMedalla() != null ? usuarioMedalla.getMedalla().getId() : null,
                usuarioMedalla.getMedalla() != null ? usuarioMedalla.getMedalla().getNombre() : null,
                usuarioMedalla.getMedalla() != null ? usuarioMedalla.getMedalla().getDescripcion() : null,
                usuarioMedalla.getMedalla() != null ? usuarioMedalla.getMedalla().getImagenUrl() : null,
                usuarioMedalla.getFechaObtencion()
        );
    }
}
