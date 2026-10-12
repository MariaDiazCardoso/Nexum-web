package com.nexum.controller;

import com.nexum.dto.IaHistorialRequest;
import com.nexum.dto.IaHistorialResponse;
import com.nexum.service.IaHistorialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-ia")
public class IaHistorialController {

    private final IaHistorialService iaHistorialService;

    public IaHistorialController(IaHistorialService iaHistorialService) {
        this.iaHistorialService = iaHistorialService;
    }

    @PostMapping
    public ResponseEntity<IaHistorialResponse> crear(@Valid @RequestBody IaHistorialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iaHistorialService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<IaHistorialResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID cursoId,
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(iaHistorialService.listar(usuarioId, cursoId, leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IaHistorialResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(iaHistorialService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IaHistorialResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody IaHistorialRequest request) {
        return ResponseEntity.ok(iaHistorialService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        iaHistorialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
