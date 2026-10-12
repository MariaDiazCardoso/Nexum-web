package com.nexum.controller;

import com.nexum.dto.MedallaRequest;
import com.nexum.dto.MedallaResponse;
import com.nexum.dto.UsuarioMedallaRequest;
import com.nexum.dto.UsuarioMedallaResponse;
import com.nexum.service.MedallaService;
import com.nexum.service.UsuarioMedallaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class MedallaController {

    private final MedallaService medallaService;
    private final UsuarioMedallaService usuarioMedallaService;

    public MedallaController(MedallaService medallaService, UsuarioMedallaService usuarioMedallaService) {
        this.medallaService = medallaService;
        this.usuarioMedallaService = usuarioMedallaService;
    }

    @PostMapping("/api/medallas")
    public ResponseEntity<MedallaResponse> crear(@Valid @RequestBody MedallaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medallaService.crear(request));
    }

    @GetMapping("/api/medallas")
    public ResponseEntity<List<MedallaResponse>> listar() {
        return ResponseEntity.ok(medallaService.listar());
    }

    @GetMapping("/api/medallas/{id}")
    public ResponseEntity<MedallaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(medallaService.obtenerPorId(id));
    }

    @PutMapping("/api/medallas/{id}")
    public ResponseEntity<MedallaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MedallaRequest request) {
        return ResponseEntity.ok(medallaService.actualizar(id, request));
    }

    @DeleteMapping("/api/medallas/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        medallaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/usuarios/{usuarioId}/medallas")
    public ResponseEntity<UsuarioMedallaResponse> asignar(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody UsuarioMedallaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMedallaService.asignar(usuarioId, request));
    }

    @GetMapping("/api/usuarios/{usuarioId}/medallas")
    public ResponseEntity<List<UsuarioMedallaResponse>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(usuarioMedallaService.listar(usuarioId));
    }

    @DeleteMapping("/api/usuarios/{usuarioId}/medallas/{medallaId}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable UUID usuarioId, @PathVariable UUID medallaId) {
        usuarioMedallaService.eliminar(usuarioId, medallaId);
        return ResponseEntity.noContent().build();
    }
}
