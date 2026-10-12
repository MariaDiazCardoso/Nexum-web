package com.nexum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SuscripcionRequest(
        @NotNull UUID usuarioId,
        @NotBlank @Size(max = 20) String plan,
        @NotNull @DecimalMin(value = "0.0") BigDecimal precio,
        Integer creditosIa,
        Integer creditosRestantes,
        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean renovacionAutomatica,
        @NotBlank @Size(max = 20) String estado
) {}
