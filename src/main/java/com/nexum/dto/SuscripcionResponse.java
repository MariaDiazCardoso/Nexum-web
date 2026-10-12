package com.nexum.dto;

import com.nexum.entity.Suscripcion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SuscripcionResponse(
        UUID id,
        UUID usuarioId,
        String plan,
        BigDecimal precio,
        Integer creditosIa,
        Integer creditosRestantes,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean renovacionAutomatica,
        String estado,
        LocalDateTime createdAt
) {
    public static SuscripcionResponse desde(Suscripcion suscripcion) {
        return new SuscripcionResponse(
                suscripcion.getId(),
                suscripcion.getUsuario() != null ? suscripcion.getUsuario().getId() : null,
                suscripcion.getPlan(),
                suscripcion.getPrecio(),
                suscripcion.getCreditosIa(),
                suscripcion.getCreditosRestantes(),
                suscripcion.getFechaInicio(),
                suscripcion.getFechaFin(),
                suscripcion.getRenovacionAutomatica(),
                suscripcion.getEstado(),
                suscripcion.getCreatedAt()
        );
    }
}
