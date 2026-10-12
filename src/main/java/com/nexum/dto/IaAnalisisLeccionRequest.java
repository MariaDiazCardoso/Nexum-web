package com.nexum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record IaAnalisisLeccionRequest(
        @NotNull UUID usuarioId,
        @NotNull UUID leccionId,
        String resumen,
        @Size(max = 30) String nivel,
        @Size(max = 50) String duracion,
        List<String> objetivos,
        String consejo,
        String siguientePaso,
        Map<String, Object> proyecto
) {}
