package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record VideoStreamingRequest(
        @NotNull UUID leccionId,
        @NotBlank @Size(max = 255) String awsS3Key,
        @Size(max = 255) String awsCloudfrontUrl,
        Integer duracionSegundos,
        @Size(max = 50) String calidadDisponible
) {}
