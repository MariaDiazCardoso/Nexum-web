package com.nexum.controller;

import com.nexum.dto.RecursoExternoCacheRequest;
import com.nexum.dto.RecursoExternoCacheResponse;
import com.nexum.service.RecursoExternoCacheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recursos-externos-cache")
public class RecursoExternoCacheController {

    private final RecursoExternoCacheService recursoExternoCacheService;

    public RecursoExternoCacheController(RecursoExternoCacheService recursoExternoCacheService) {
        this.recursoExternoCacheService = recursoExternoCacheService;
    }

    @PostMapping
    public ResponseEntity<RecursoExternoCacheResponse> crear(
            @Valid @RequestBody RecursoExternoCacheRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoExternoCacheService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<RecursoExternoCacheResponse>> listar(
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(recursoExternoCacheService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoExternoCacheResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(recursoExternoCacheService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoExternoCacheResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RecursoExternoCacheRequest request) {
        return ResponseEntity.ok(recursoExternoCacheService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        recursoExternoCacheService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
