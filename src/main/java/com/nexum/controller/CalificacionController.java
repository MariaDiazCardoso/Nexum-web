package com.nexum.controller;

import com.nexum.dto.CalificacionRequest;
import com.nexum.dto.CalificacionResponse;
import com.nexum.service.CalificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    public ResponseEntity<CalificacionResponse> crear(@Valid @RequestBody CalificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calificacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CalificacionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID cursoId) {
        return ResponseEntity.ok(calificacionService.listar(usuarioId, cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(calificacionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CalificacionRequest request) {
        return ResponseEntity.ok(calificacionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        calificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
