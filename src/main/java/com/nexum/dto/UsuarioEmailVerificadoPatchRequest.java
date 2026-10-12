package com.nexum.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioEmailVerificadoPatchRequest(

        @NotNull(message = "emailVerificado es obligatorio")
        Boolean emailVerificado
) {}
