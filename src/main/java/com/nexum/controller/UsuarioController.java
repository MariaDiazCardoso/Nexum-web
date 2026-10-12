package com.nexum.controller;

import com.nexum.dto.*;
import com.nexum.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST del módulo Usuario (T03).
 * Ruta base: /api/usuarios
 * Inyección por constructor sobre la interfaz {@link UsuarioService}.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/usuarios → 201
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody UsuarioRegistroRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.registrar(request));
    }

    // GET /api/usuarios → 200
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    // GET /api/usuarios/{id} → 200 | 404
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @accesoService.esPropio(#id)")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // PUT /api/usuarios/{id} → 200 | 400 | 404 | 409
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @accesoService.esPropio(#id)")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequest request) {

        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    // PATCH /api/usuarios/{id} → 200 | 400 | 404 | 409
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @accesoService.esPropio(#id)")
    public ResponseEntity<UsuarioResponse> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioPatchRequest request) {

        return ResponseEntity.ok(usuarioService.actualizarParcial(id, request));
    }

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> actualizarRol(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRolPatchRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarRol(id, request));
    }

    @PatchMapping("/{id}/email-verificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> actualizarEmailVerificado(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioEmailVerificadoPatchRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarEmailVerificado(id, request));
    }

    // DELETE /api/usuarios/{id} → 204 | 404
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
