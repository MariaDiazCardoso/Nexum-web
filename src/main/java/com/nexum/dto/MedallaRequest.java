package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedallaRequest(
        @NotBlank @Size(max = 100) String nombre,
        String descripcion,
        @Size(max = 255) String imagenUrl
) {}
