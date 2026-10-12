package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ComentarioLeccionRequest(
        @NotNull UUID usuarioId,
        @NotNull UUID leccionId,
        UUID comentarioPadreId,
        @NotBlank String contenido
) {}
