package com.nexum.controller;

import com.nexum.dto.SeguimientoLeccionPatchRequest;
import com.nexum.dto.SeguimientoLeccionRequest;
import com.nexum.dto.SeguimientoLeccionResponse;
import com.nexum.service.SeguimientoLeccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seguimiento-lecciones")
public class SeguimientoLeccionController {

    private final SeguimientoLeccionService seguimientoLeccionService;

    public SeguimientoLeccionController(SeguimientoLeccionService seguimientoLeccionService) {
        this.seguimientoLeccionService = seguimientoLeccionService;
    }

    @PostMapping
    public ResponseEntity<SeguimientoLeccionResponse> crear(
            @Valid @RequestBody SeguimientoLeccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seguimientoLeccionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<SeguimientoLeccionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(seguimientoLeccionService.listar(usuarioId, leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoLeccionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(seguimientoLeccionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeguimientoLeccionResponse> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody SeguimientoLeccionPatchRequest request) {
        return ResponseEntity.ok(seguimientoLeccionService.actualizarParcial(id, request));
    }
}
