package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ActividadRequest(

        @NotNull(message = "El leccionId es obligatorio")
        UUID leccionId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String titulo,

        String instrucciones,

        @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
        String tipo
) {}
