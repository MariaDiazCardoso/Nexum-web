package com.nexum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InscripcionRequest(

        @NotNull(message = "El usuarioId es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El cursoId es obligatorio")
        UUID cursoId,

        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}
