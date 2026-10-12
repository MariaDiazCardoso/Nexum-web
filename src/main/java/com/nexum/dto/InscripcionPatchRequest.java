package com.nexum.dto;

import jakarta.validation.constraints.Size;

public record InscripcionPatchRequest(

        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}
