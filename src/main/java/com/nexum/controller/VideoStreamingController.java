package com.nexum.controller;

import com.nexum.dto.VideoStreamingRequest;
import com.nexum.dto.VideoStreamingResponse;
import com.nexum.service.VideoStreamingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos-streaming")
public class VideoStreamingController {

    private final VideoStreamingService videoStreamingService;

    public VideoStreamingController(VideoStreamingService videoStreamingService) {
        this.videoStreamingService = videoStreamingService;
    }

    @PostMapping
    public ResponseEntity<VideoStreamingResponse> crear(@Valid @RequestBody VideoStreamingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoStreamingService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<VideoStreamingResponse>> listar(@RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(videoStreamingService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoStreamingResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(videoStreamingService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoStreamingResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody VideoStreamingRequest request) {
        return ResponseEntity.ok(videoStreamingService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        videoStreamingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
