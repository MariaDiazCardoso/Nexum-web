package com.nexum.dto;

import com.nexum.entity.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagoResponse(
        UUID id,
        UUID usuarioId,
        UUID suscripcionId,
        BigDecimal monto,
        String metodo,
        String estado,
        String concepto,
        String referencia,
        LocalDateTime fechaPago
) {
    public static PagoResponse desde(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getUsuario() != null ? pago.getUsuario().getId() : null,
                pago.getSuscripcion() != null ? pago.getSuscripcion().getId() : null,
                pago.getMonto(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getConcepto(),
                pago.getReferencia(),
                pago.getFechaPago()
        );
    }
}
