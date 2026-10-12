package com.nexum.controller;

import com.nexum.dto.ProgresoLeccionPatchRequest;
import com.nexum.dto.ProgresoLeccionRequest;
import com.nexum.dto.ProgresoLeccionResponse;
import com.nexum.service.ProgresoLeccionService;
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
@RequestMapping("/api/progresos-leccion")
public class ProgresoLeccionController {

    private final ProgresoLeccionService progresoLeccionService;

    public ProgresoLeccionController(ProgresoLeccionService progresoLeccionService) {
        this.progresoLeccionService = progresoLeccionService;
    }

    @PostMapping
    public ResponseEntity<ProgresoLeccionResponse> crear(
            @Valid @RequestBody ProgresoLeccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progresoLeccionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ProgresoLeccionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) UUID cursoId,
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(progresoLeccionService.listar(usuarioId, cursoId, leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgresoLeccionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(progresoLeccionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProgresoLeccionResponse> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody ProgresoLeccionPatchRequest request) {
        return ResponseEntity.ok(progresoLeccionService.actualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        progresoLeccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
