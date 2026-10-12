package com.nexum.service;

import com.nexum.dto.RecursoAdjuntoRequest;
import com.nexum.dto.RecursoAdjuntoResponse;

import java.util.List;
import java.util.UUID;

public interface RecursoAdjuntoService {
    RecursoAdjuntoResponse crear(RecursoAdjuntoRequest request);
    List<RecursoAdjuntoResponse> listar(UUID leccionId);
    RecursoAdjuntoResponse obtenerPorId(UUID id);
    RecursoAdjuntoResponse actualizar(UUID id, RecursoAdjuntoRequest request);
    void eliminar(UUID id);
}
