package com.nexum.service;

import com.nexum.dto.CalificacionRequest;
import com.nexum.dto.CalificacionResponse;

import java.util.List;
import java.util.UUID;

public interface CalificacionService {

    CalificacionResponse crear(CalificacionRequest request);

    List<CalificacionResponse> listar(UUID usuarioId, UUID cursoId);

    CalificacionResponse obtenerPorId(UUID id);

    CalificacionResponse actualizar(UUID id, CalificacionRequest request);

    void eliminar(UUID id);
}
