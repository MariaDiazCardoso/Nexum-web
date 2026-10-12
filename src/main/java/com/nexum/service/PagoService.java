package com.nexum.service;

import com.nexum.dto.PagoRequest;
import com.nexum.dto.PagoResponse;

import java.util.List;
import java.util.UUID;

public interface PagoService {
    PagoResponse crear(PagoRequest request);
    List<PagoResponse> listar(UUID usuarioId);
    PagoResponse obtenerPorId(UUID id);
    PagoResponse actualizar(UUID id, PagoRequest request);
    void eliminar(UUID id);
}
