package com.nexum.controller;

import com.nexum.dto.ExamenRequest;
import com.nexum.dto.ExamenResponse;
import com.nexum.service.ExamenService;
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
@RequestMapping("/api/examenes")
public class ExamenController {

    private final ExamenService examenService;

    public ExamenController(ExamenService examenService) {
        this.examenService = examenService;
    }

    @PostMapping
    public ResponseEntity<ExamenResponse> crear(@Valid @RequestBody ExamenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examenService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ExamenResponse>> listar(
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(examenService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamenResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(examenService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamenResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ExamenRequest request) {
        return ResponseEntity.ok(examenService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        examenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
