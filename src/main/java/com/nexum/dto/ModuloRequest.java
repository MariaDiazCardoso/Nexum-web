package com.nexum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModuloRequest(

        @NotNull(message = "El cursoId es obligatorio")
        UUID cursoId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String titulo,

        @NotNull(message = "El orden es obligatorio")
        @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
        Integer orden,

        String descripcion,

        @Size(max = 180, message = "El slug no puede superar los 180 caracteres")
        String slug
) {}
