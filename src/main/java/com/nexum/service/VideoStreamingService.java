package com.nexum.service;

import com.nexum.dto.VideoStreamingRequest;
import com.nexum.dto.VideoStreamingResponse;

import java.util.List;
import java.util.UUID;

public interface VideoStreamingService {
    VideoStreamingResponse crear(VideoStreamingRequest request);
    List<VideoStreamingResponse> listar(UUID leccionId);
    VideoStreamingResponse obtenerPorId(UUID id);
    VideoStreamingResponse actualizar(UUID id, VideoStreamingRequest request);
    void eliminar(UUID id);
}
