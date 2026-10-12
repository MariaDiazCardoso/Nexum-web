package com.nexum.service;

import com.nexum.dto.CursoPatchRequest;
import com.nexum.dto.CursoRequest;
import com.nexum.dto.CursoResponse;
import com.nexum.entity.EstadoCurso;

import java.util.List;
import java.util.UUID;

/** Contrato de negocio para el módulo Curso (T05). */
public interface CursoService {

    CursoResponse crear(CursoRequest request);

    List<CursoResponse> listar(UUID categoriaId, EstadoCurso estado);

    List<CursoResponse> listarPorCategoria(String categoria, EstadoCurso estado);

    CursoResponse obtenerPorId(UUID id);

    CursoResponse actualizar(UUID id, CursoRequest request);

    CursoResponse actualizarParcial(UUID id, CursoPatchRequest request);

    void eliminar(UUID id);
}

