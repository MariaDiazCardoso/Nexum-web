package com.nexum.controller;

import com.nexum.dto.ActividadRequest;
import com.nexum.dto.ActividadResponse;
import com.nexum.service.ActividadService;
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
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @PostMapping
    public ResponseEntity<ActividadResponse> crear(@Valid @RequestBody ActividadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(actividadService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ActividadResponse>> listar(
            @RequestParam(required = false) UUID leccionId) {
        return ResponseEntity.ok(actividadService.listar(leccionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(actividadService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActividadRequest request) {
        return ResponseEntity.ok(actividadService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
