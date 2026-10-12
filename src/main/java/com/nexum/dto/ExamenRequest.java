package com.nexum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ExamenRequest(

        @NotNull(message = "El leccionId es obligatorio")
        UUID leccionId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String titulo,

        @Min(value = 1, message = "Los intentos máximos deben ser mayores o iguales a 1")
        Integer intentosMaximos,

        @DecimalMin(value = "0.0", message = "El puntaje mínimo de aprobación no puede ser negativo")
        BigDecimal puntajeMinimoAprobacion
) {}
