package com.nexum.controller;

import com.nexum.dto.SuscripcionRequest;
import com.nexum.dto.SuscripcionResponse;
import com.nexum.service.SuscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @PostMapping
    public ResponseEntity<SuscripcionResponse> crear(@Valid @RequestBody SuscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<SuscripcionResponse>> listar(@RequestParam(required = false) UUID usuarioId) {
        return ResponseEntity.ok(suscripcionService.listar(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(suscripcionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuscripcionResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody SuscripcionRequest request) {
        return ResponseEntity.ok(suscripcionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        suscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
