package com.nexum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PreguntaRequest(

        @NotNull(message = "El examenId es obligatorio")
        UUID examenId,

        @NotBlank(message = "El texto de la pregunta es obligatorio")
        String textoPregunta,

        String explicacionRespuesta,

        @NotNull(message = "La lista de opciones es obligatoria")
        List<@Valid OpcionRespuestaRequest> opciones
) {}
