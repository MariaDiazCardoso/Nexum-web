package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RecursoAdjuntoRequest(
        @NotNull UUID leccionId,
        @NotBlank @Size(max = 255) String nombre,
        @NotBlank @Size(max = 255) String urlArchivo,
        @Size(max = 50) String tipoArchivo
) {}
