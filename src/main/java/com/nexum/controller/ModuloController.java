package com.nexum.controller;

import com.nexum.dto.ModuloPatchRequest;
import com.nexum.dto.ModuloRequest;
import com.nexum.dto.ModuloResponse;
import com.nexum.service.ModuloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/modulos")
public class ModuloController {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @PostMapping
    public ResponseEntity<ModuloResponse> crear(@Valid @RequestBody ModuloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduloService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ModuloResponse>> listar(
            @RequestParam(required = false) UUID cursoId) {
        return ResponseEntity.ok(moduloService.listar(cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(moduloService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ModuloRequest request) {
        return ResponseEntity.ok(moduloService.actualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ModuloResponse> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody ModuloPatchRequest request) {
        return ResponseEntity.ok(moduloService.actualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        moduloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
