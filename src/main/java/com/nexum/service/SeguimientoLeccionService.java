package com.nexum.service;

import com.nexum.dto.SeguimientoLeccionPatchRequest;
import com.nexum.dto.SeguimientoLeccionRequest;
import com.nexum.dto.SeguimientoLeccionResponse;

import java.util.List;
import java.util.UUID;

public interface SeguimientoLeccionService {

    SeguimientoLeccionResponse crear(SeguimientoLeccionRequest request);

    List<SeguimientoLeccionResponse> listar(UUID usuarioId, UUID leccionId);

    SeguimientoLeccionResponse obtenerPorId(UUID id);

    SeguimientoLeccionResponse actualizarParcial(UUID id, SeguimientoLeccionPatchRequest request);
}
