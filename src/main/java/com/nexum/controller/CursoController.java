package com.nexum.controller;

import com.nexum.dto.CursoPatchRequest;
import com.nexum.dto.CursoRequest;
import com.nexum.dto.CursoResponse;
import com.nexum.entity.EstadoCurso;
import com.nexum.service.CursoService;
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
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> crear(@Valid @RequestBody CursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cursoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar(
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) EstadoCurso estado) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(cursoService.listarPorCategoria(categoria, estado));
        }
        return ResponseEntity.ok(cursoService.listar(categoriaId, estado));
    }

    public ResponseEntity<List<CursoResponse>> listar(UUID categoriaId, EstadoCurso estado) {
        return listar(categoriaId, null, estado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(cursoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoService.actualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CursoResponse> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody CursoPatchRequest request) {
        return ResponseEntity.ok(cursoService.actualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
