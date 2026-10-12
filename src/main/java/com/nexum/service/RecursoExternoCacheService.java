package com.nexum.service;

import com.nexum.dto.RecursoExternoCacheRequest;
import com.nexum.dto.RecursoExternoCacheResponse;

import java.util.List;
import java.util.UUID;

public interface RecursoExternoCacheService {
    RecursoExternoCacheResponse crear(RecursoExternoCacheRequest request);
    List<RecursoExternoCacheResponse> listar(UUID leccionId);
    RecursoExternoCacheResponse obtenerPorId(UUID id);
    RecursoExternoCacheResponse actualizar(UUID id, RecursoExternoCacheRequest request);
    void eliminar(UUID id);
}
