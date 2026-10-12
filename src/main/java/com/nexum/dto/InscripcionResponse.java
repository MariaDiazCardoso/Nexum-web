package com.nexum.dto;

import com.nexum.entity.Inscripcion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InscripcionResponse(
        UUID id,
        UUID usuarioId,
        UUID cursoId,
        String estado,
        BigDecimal progresoPorcentaje,
        LocalDateTime fechaInscripcion
) {
    public static InscripcionResponse desde(Inscripcion inscripcion) {
        return new InscripcionResponse(
                inscripcion.getId(),
                inscripcion.getUsuario() != null ? inscripcion.getUsuario().getId() : null,
                inscripcion.getCurso() != null ? inscripcion.getCurso().getId() : null,
                inscripcion.getEstado(),
                inscripcion.getProgresoPorcentaje(),
                inscripcion.getFechaInscripcion()
        );
    }
}
