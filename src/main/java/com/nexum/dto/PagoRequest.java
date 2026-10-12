package com.nexum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PagoRequest(
        @NotNull UUID usuarioId,
        UUID suscripcionId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal monto,
        @NotBlank @Size(max = 30) String metodo,
        @NotBlank @Size(max = 20) String estado,
        @Size(max = 80) String concepto,
        @Size(max = 120) String referencia
) {}
