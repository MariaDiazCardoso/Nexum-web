package com.nexum.controller;

import com.nexum.dto.LeccionRequest;
import com.nexum.dto.LeccionResponse;
import com.nexum.service.LeccionService;
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
@RequestMapping("/api/lecciones")
public class LeccionController {

    private final LeccionService leccionService;

    public LeccionController(LeccionService leccionService) {
        this.leccionService = leccionService;
    }

    @PostMapping
    public ResponseEntity<LeccionResponse> crear(@Valid @RequestBody LeccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leccionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<LeccionResponse>> listar(
            @RequestParam(required = false) UUID moduloId) {
        return ResponseEntity.ok(leccionService.listar(moduloId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeccionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(leccionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeccionResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody LeccionRequest request) {
        return ResponseEntity.ok(leccionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        leccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
