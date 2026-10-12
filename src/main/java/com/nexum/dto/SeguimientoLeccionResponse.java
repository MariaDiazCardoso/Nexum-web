package com.nexum.dto;

import com.nexum.entity.SeguimientoLeccion;

import java.time.LocalDateTime;
import java.util.UUID;

public record SeguimientoLeccionResponse(
        UUID id,
        UUID usuarioId,
        UUID leccionId,
        Integer tiempoAcumulado,
        Integer cantidadVisitas,
        Integer intentosPractica,
        LocalDateTime primeraActividad,
        LocalDateTime ultimaActividad
) {
    public static SeguimientoLeccionResponse desde(SeguimientoLeccion seguimiento) {
        return new SeguimientoLeccionResponse(
                seguimiento.getId(),
                seguimiento.getUsuario() != null ? seguimiento.getUsuario().getId() : null,
                seguimiento.getLeccion() != null ? seguimiento.getLeccion().getId() : null,
                seguimiento.getTiempoAcumulado(),
                seguimiento.getCantidadVisitas(),
                seguimiento.getIntentosPractica(),
                seguimiento.getPrimeraActividad(),
                seguimiento.getUltimaActividad()
        );
    }
}
