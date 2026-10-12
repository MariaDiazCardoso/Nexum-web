package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpcionRespuestaRequest(

        @NotBlank(message = "El texto de la opción es obligatorio")
        String textoOpcion,

        @NotNull(message = "Debe indicar si la opción es correcta")
        Boolean esCorrecta
) {}
