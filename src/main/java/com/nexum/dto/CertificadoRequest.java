package com.nexum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CertificadoRequest(

        @NotNull(message = "El inscripcionId es obligatorio")
        UUID inscripcionId,

        @Size(max = 255, message = "La URL del PDF no puede superar los 255 caracteres")
        String urlPdf
) {}
