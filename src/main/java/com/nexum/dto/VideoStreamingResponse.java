package com.nexum.dto;

import com.nexum.entity.VideoStreaming;

import java.util.UUID;

public record VideoStreamingResponse(
        UUID id,
        UUID leccionId,
        String awsS3Key,
        String awsCloudfrontUrl,
        Integer duracionSegundos,
        String calidadDisponible
) {
    public static VideoStreamingResponse desde(VideoStreaming video) {
        return new VideoStreamingResponse(
                video.getId(),
                video.getLeccion() != null ? video.getLeccion().getId() : null,
                video.getAwsS3Key(),
                video.getAwsCloudfrontUrl(),
                video.getDuracionSegundos(),
                video.getCalidadDisponible()
        );
    }
}
