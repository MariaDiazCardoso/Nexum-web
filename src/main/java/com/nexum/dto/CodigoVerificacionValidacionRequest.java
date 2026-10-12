package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;

public record CodigoVerificacionValidacionRequest(
        @NotBlank String codigo
) {}
