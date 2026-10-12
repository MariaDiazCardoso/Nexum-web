package com.nexum.controller;

import com.nexum.dto.PreguntaRequest;
import com.nexum.dto.PreguntaResponse;
import com.nexum.service.PreguntaService;
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
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @PostMapping
    public ResponseEntity<PreguntaResponse> crear(@Valid @RequestBody PreguntaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preguntaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<PreguntaResponse>> listar(
            @RequestParam(required = false) UUID examenId) {
        return ResponseEntity.ok(preguntaService.listar(examenId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(preguntaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody PreguntaRequest request) {
        return ResponseEntity.ok(preguntaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        preguntaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
