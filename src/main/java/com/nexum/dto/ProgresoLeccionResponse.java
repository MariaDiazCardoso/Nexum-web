package com.nexum.dto;

import com.nexum.entity.ProgresoLeccion;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgresoLeccionResponse(
        UUID id,
        UUID usuarioId,
        UUID leccionId,
        Boolean completado,
        Integer ultimoSegundoVisto,
        LocalDateTime fechaCompletado,
        Integer tiempoActivo
) {
    public static ProgresoLeccionResponse desde(ProgresoLeccion progreso) {
        return new ProgresoLeccionResponse(
                progreso.getId(),
                progreso.getUsuario() != null ? progreso.getUsuario().getId() : null,
                progreso.getLeccion() != null ? progreso.getLeccion().getId() : null,
                progreso.getCompletado(),
                progreso.getUltimoSegundoVisto(),
                progreso.getFechaCompletado(),
                progreso.getTiempoActivo()
        );
    }
}
