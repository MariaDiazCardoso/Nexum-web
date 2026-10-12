package com.nexum.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProgresoLeccionRequest(

        @NotNull(message = "El usuarioId es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El leccionId es obligatorio")
        UUID leccionId,

        Boolean completado,

        Integer ultimoSegundoVisto,

        Integer tiempoActivo
) {}
