package com.nexum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record LeccionRequest(

        @NotNull(message = "El moduloId es obligatorio")
        UUID moduloId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String titulo,

        @Size(max = 20, message = "El tipo no puede superar los 20 caracteres")
        String tipo,

        @NotNull(message = "El orden es obligatorio")
        @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
        Integer orden,

        String contenidoTexto,

        Integer puntosOtorgados,

        String videoUrl,

        Integer xpOtorgada,

        @Size(max = 180, message = "El slug no puede superar los 180 caracteres")
        String slug,

        String retoPractico,

        @Size(max = 20, message = "La dificultad no puede superar los 20 caracteres")
        String dificultad,

        @Size(max = 30, message = "La duración estimada no puede superar los 30 caracteres")
        String duracionEstimada,

        Integer duracionMinima,

        String imagenUrl
) {}
