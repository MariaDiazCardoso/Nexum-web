package com.nexum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CalificacionRequest(

        @NotNull(message = "El usuarioId es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El cursoId es obligatorio")
        UUID cursoId,

        UUID leccionId,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
        String tipo,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String titulo,

        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0.0", message = "La nota no puede ser negativa")
        BigDecimal nota,

        @NotNull(message = "La nota máxima es obligatoria")
        @DecimalMin(value = "0.01", message = "La nota máxima debe ser mayor a 0")
        BigDecimal notaMaxima,

        @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
        BigDecimal peso,

        String comentario
) {}
