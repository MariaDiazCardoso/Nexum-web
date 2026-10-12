package com.nexum.controller;

import com.nexum.dto.InscripcionPatchRequest;
import com.nexum.dto.InscripcionRequest;
import com.nexum.dto.InscripcionResponse;
import com.nexum.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    public ResponseEntity<InscripcionResponse> crear(@Valid @RequestBody InscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscripcionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID cursoId) {
        return ResponseEntity.ok(inscripcionService.listar(usuarioId, cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InscripcionResponse> actualizarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody InscripcionPatchRequest request) {
        return ResponseEntity.ok(inscripcionService.actualizarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        inscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
