package com.nexum.dto;

import com.nexum.entity.Actividad;

import java.util.UUID;

public record ActividadResponse(
        UUID id,
        UUID leccionId,
        String titulo,
        String instrucciones,
        String tipo
) {
    public static ActividadResponse desde(Actividad actividad) {
        return new ActividadResponse(
                actividad.getId(),
                actividad.getLeccion() != null ? actividad.getLeccion().getId() : null,
                actividad.getTitulo(),
                actividad.getInstrucciones(),
                actividad.getTipo()
        );
    }
}
