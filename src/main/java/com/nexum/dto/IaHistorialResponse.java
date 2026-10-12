package com.nexum.dto;

import com.nexum.entity.IaHistorial;

import java.time.LocalDateTime;
import java.util.UUID;

public record IaHistorialResponse(
        Integer id,
        UUID usuarioId,
        UUID cursoId,
        UUID leccionId,
        String rol,
        String mensaje,
        LocalDateTime fechaCreacion
) {
    public static IaHistorialResponse desde(IaHistorial historial) {
        return new IaHistorialResponse(
                historial.getId(),
                historial.getUsuario() != null ? historial.getUsuario().getId() : null,
                historial.getCurso() != null ? historial.getCurso().getId() : null,
                historial.getLeccion() != null ? historial.getLeccion().getId() : null,
                historial.getRol(),
                historial.getMensaje(),
                historial.getFechaCreacion()
        );
    }
}
