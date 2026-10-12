package com.nexum.controller;

import com.nexum.dto.ComentarioLeccionRequest;
import com.nexum.dto.ComentarioLeccionResponse;
import com.nexum.service.ComentarioLeccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comentarios-leccion")
public class ComentarioLeccionController {

    private final ComentarioLeccionService comentarioLeccionService;

    public ComentarioLeccionController(ComentarioLeccionService comentarioLeccionService) {
        this.comentarioLeccionService = comentarioLeccionService;
    }

    @PostMapping
    public ResponseEntity<ComentarioLeccionResponse> crear(@Valid @RequestBody ComentarioLeccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioLeccionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ComentarioLeccionResponse>> listar(@RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(comentarioLeccionService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioLeccionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(comentarioLeccionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioLeccionResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ComentarioLeccionRequest request) {
        return ResponseEntity.ok(comentarioLeccionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        comentarioLeccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
