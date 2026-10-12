package com.nexum.controller;

import com.nexum.dto.RecursoAdjuntoRequest;
import com.nexum.dto.RecursoAdjuntoResponse;
import com.nexum.service.RecursoAdjuntoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recursos-adjuntos")
public class RecursoAdjuntoController {

    private final RecursoAdjuntoService recursoAdjuntoService;

    public RecursoAdjuntoController(RecursoAdjuntoService recursoAdjuntoService) {
        this.recursoAdjuntoService = recursoAdjuntoService;
    }

    @PostMapping
    public ResponseEntity<RecursoAdjuntoResponse> crear(@Valid @RequestBody RecursoAdjuntoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoAdjuntoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<RecursoAdjuntoResponse>> listar(@RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(recursoAdjuntoService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoAdjuntoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(recursoAdjuntoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoAdjuntoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RecursoAdjuntoRequest request) {
        return ResponseEntity.ok(recursoAdjuntoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        recursoAdjuntoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
