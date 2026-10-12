package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record IaHistorialRequest(
        @NotNull UUID usuarioId,
        UUID cursoId,
        UUID leccionId,
        @NotBlank @Size(max = 20) String rol,
        @NotBlank String mensaje
) {}
