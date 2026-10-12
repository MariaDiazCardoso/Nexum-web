package com.nexum.dto;

import com.nexum.entity.CodigoVerificacion;

import java.time.LocalDateTime;
import java.util.UUID;

public record CodigoVerificacionResponse(
        UUID id,
        UUID usuarioId,
        String tipo,
        Boolean usado,
        LocalDateTime expiraEn,
        LocalDateTime fechaCreacion,
        String codigo
) {
    public static CodigoVerificacionResponse desde(CodigoVerificacion codigoVerificacion, String codigoClaro) {
        return new CodigoVerificacionResponse(
                codigoVerificacion.getId(),
                codigoVerificacion.getUsuario() != null ? codigoVerificacion.getUsuario().getId() : null,
                codigoVerificacion.getTipo(),
                codigoVerificacion.getUsado(),
                codigoVerificacion.getExpiraEn(),
                codigoVerificacion.getFechaCreacion(),
                codigoClaro
        );
    }
}
