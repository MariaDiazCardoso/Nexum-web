package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record RecursoExternoCacheRequest(
        @NotNull UUID leccionId,
        @Size(max = 30) String tipo,
        String titulo,
        String descripcion,
        @NotBlank String url,
        String thumbnail,
        @Size(max = 50) String fuente,
        Map<String, Object> metadata
) {}
