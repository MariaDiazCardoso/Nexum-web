package com.nexum.service;

import com.nexum.dto.ComentarioLeccionRequest;
import com.nexum.dto.ComentarioLeccionResponse;

import java.util.List;
import java.util.UUID;

public interface ComentarioLeccionService {
    ComentarioLeccionResponse crear(ComentarioLeccionRequest request);
    List<ComentarioLeccionResponse> listar(UUID leccionId);
    ComentarioLeccionResponse obtenerPorId(UUID id);
    ComentarioLeccionResponse actualizar(UUID id, ComentarioLeccionRequest request);
    void eliminar(UUID id);
}
