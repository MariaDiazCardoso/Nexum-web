package com.nexum.service;

import com.nexum.dto.CategoriaRequest;
import com.nexum.dto.CategoriaResponse;
import com.nexum.entity.Categoria;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.CategoriaRepository;
import com.nexum.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de Categoria (T04).
 *
 * <p>Reglas de negocio:
 * <ul>
 *   <li>DELETE → 409 si tiene cursos asociados.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .iconoUrl(request.iconoUrl())
                .build();
        return CategoriaResponse.desde(categoriaRepository.save(categoria));
    }

    @Override
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponse::desde)
                .toList();
    }

    @Override
    public CategoriaResponse obtenerPorId(UUID id) {
        return CategoriaResponse.desde(buscarOLanzar(id));
    }

    @Override
    public CategoriaResponse actualizar(UUID id, CategoriaRequest request) {
        Categoria categoria = buscarOLanzar(id);
        categoria.setNombre(request.nombre());
        categoria.setIconoUrl(request.iconoUrl());
        return CategoriaResponse.desde(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(UUID id) {
        buscarOLanzar(id);
        if (cursoRepository.existsByCategorias_Id(id)) {
            throw new ConflictoException(
                    "No se puede eliminar la categoría porque tiene cursos asociados");
        }
        categoriaRepository.deleteById(id);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Categoria buscarOLanzar(UUID id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + id));
    }
}

