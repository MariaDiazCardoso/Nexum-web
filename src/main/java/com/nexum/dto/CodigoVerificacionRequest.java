package com.nexum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CodigoVerificacionRequest(
        @NotNull UUID usuarioId,
        @NotBlank @Size(max = 30) String tipo,
        @NotNull @Min(1) Integer minutosVigencia
) {}
