package com.nexum.service;

import com.nexum.dto.SuscripcionRequest;
import com.nexum.dto.SuscripcionResponse;

import java.util.List;
import java.util.UUID;

public interface SuscripcionService {
    SuscripcionResponse crear(SuscripcionRequest request);
    List<SuscripcionResponse> listar(UUID usuarioId);
    SuscripcionResponse obtenerPorId(UUID id);
    SuscripcionResponse actualizar(UUID id, SuscripcionRequest request);
    void eliminar(UUID id);
}
