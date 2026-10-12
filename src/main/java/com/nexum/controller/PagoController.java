package com.nexum.controller;

import com.nexum.dto.PagoRequest;
import com.nexum.dto.PagoResponse;
import com.nexum.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<PagoResponse>> listar(@RequestParam(required = false) UUID usuarioId) {
        return ResponseEntity.ok(pagoService.listar(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(pagoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
