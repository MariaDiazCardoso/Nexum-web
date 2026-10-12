package com.nexum.dto;

import com.nexum.entity.Medalla;

import java.util.UUID;

public record MedallaResponse(
        UUID id,
        String nombre,
        String descripcion,
        String imagenUrl
) {
    public static MedallaResponse desde(Medalla medalla) {
        return new MedallaResponse(
                medalla.getId(),
                medalla.getNombre(),
                medalla.getDescripcion(),
                medalla.getImagenUrl()
        );
    }
}
