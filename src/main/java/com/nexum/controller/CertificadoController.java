package com.nexum.controller;

import com.nexum.dto.CertificadoRequest;
import com.nexum.dto.CertificadoResponse;
import com.nexum.service.CertificadoService;
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
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @PostMapping
    public ResponseEntity<CertificadoResponse> crear(@Valid @RequestBody CertificadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificadoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CertificadoResponse>> listar(
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String codigo) {
        return ResponseEntity.ok(certificadoService.listar(usuarioId, codigo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(certificadoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificadoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CertificadoRequest request) {
        return ResponseEntity.ok(certificadoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        certificadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
