package com.nexum.controller;

import com.nexum.dto.CodigoVerificacionRequest;
import com.nexum.dto.CodigoVerificacionResponse;
import com.nexum.dto.CodigoVerificacionValidacionRequest;
import com.nexum.service.CodigoVerificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/codigos-verificacion")
public class CodigoVerificacionController {

    private final CodigoVerificacionService codigoVerificacionService;

    public CodigoVerificacionController(CodigoVerificacionService codigoVerificacionService) {
        this.codigoVerificacionService = codigoVerificacionService;
    }

    @PostMapping
    public ResponseEntity<CodigoVerificacionResponse> generar(
            @Valid @RequestBody CodigoVerificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(codigoVerificacionService.generar(request));
    }

    @GetMapping
    public ResponseEntity<List<CodigoVerificacionResponse>> listar(
            @RequestParam(required = false) UUID usuarioId) {
        return ResponseEntity.ok(codigoVerificacionService.listar(usuarioId));
    }

    @PostMapping("/{id}/validacion")
    public ResponseEntity<CodigoVerificacionResponse> validar(
            @PathVariable UUID id,
            @Valid @RequestBody CodigoVerificacionValidacionRequest request) {
        return ResponseEntity.ok(codigoVerificacionService.validar(id, request));
    }
}
