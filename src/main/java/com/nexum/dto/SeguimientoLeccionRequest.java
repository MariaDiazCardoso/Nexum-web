package com.nexum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SeguimientoLeccionRequest(

        @NotNull(message = "El usuarioId es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El leccionId es obligatorio")
        UUID leccionId,

        @Min(value = 0, message = "El tiempo acumulado no puede ser negativo")
        Integer tiempoAcumulado,

        @Min(value = 0, message = "La cantidad de visitas no puede ser negativa")
        Integer cantidadVisitas,

        @Min(value = 0, message = "Los intentos de práctica no pueden ser negativos")
        Integer intentosPractica
) {}
