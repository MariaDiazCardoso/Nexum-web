package com.nexum.dto;

import com.nexum.entity.Examen;

import java.math.BigDecimal;
import java.util.UUID;

public record ExamenResponse(
        UUID id,
        UUID leccionId,
        String titulo,
        Integer intentosMaximos,
        BigDecimal puntajeMinimoAprobacion
) {
    public static ExamenResponse desde(Examen examen) {
        return new ExamenResponse(
                examen.getId(),
                examen.getLeccion() != null ? examen.getLeccion().getId() : null,
                examen.getTitulo(),
                examen.getIntentosMaximos(),
                examen.getPuntajeMinimoAprobacion()
        );
    }
}
