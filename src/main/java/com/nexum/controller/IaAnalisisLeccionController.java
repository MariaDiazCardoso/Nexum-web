package com.nexum.controller;

import com.nexum.dto.IaAnalisisLeccionRequest;
import com.nexum.dto.IaAnalisisLeccionResponse;
import com.nexum.service.IaAnalisisLeccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analisis-leccion")
public class IaAnalisisLeccionController {

    private final IaAnalisisLeccionService iaAnalisisLeccionService;

    public IaAnalisisLeccionController(IaAnalisisLeccionService iaAnalisisLeccionService) {
        this.iaAnalisisLeccionService = iaAnalisisLeccionService;
    }

    @PostMapping
    public ResponseEntity<IaAnalisisLeccionResponse> crear(
            @Valid @RequestBody IaAnalisisLeccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iaAnalisisLeccionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<IaAnalisisLeccionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(iaAnalisisLeccionService.listar(usuarioId, leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IaAnalisisLeccionResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(iaAnalisisLeccionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IaAnalisisLeccionResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody IaAnalisisLeccionRequest request) {
        return ResponseEntity.ok(iaAnalisisLeccionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        iaAnalisisLeccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
