package com.nexum.controller;

import com.nexum.dto.CategoriaRequest;
import com.nexum.dto.CategoriaResponse;
import com.nexum.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST del módulo Categoria (T04).
 * Ruta base: /api/categorias
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

