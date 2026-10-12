package com.nexum.service;

import com.nexum.dto.ProgresoLeccionPatchRequest;
import com.nexum.dto.ProgresoLeccionRequest;
import com.nexum.dto.ProgresoLeccionResponse;

import java.util.List;
import java.util.UUID;

public interface ProgresoLeccionService {

    ProgresoLeccionResponse crear(ProgresoLeccionRequest request);

    List<ProgresoLeccionResponse> listar(UUID usuarioId, UUID cursoId, UUID leccionId);

    ProgresoLeccionResponse obtenerPorId(UUID id);

    ProgresoLeccionResponse actualizarParcial(UUID id, ProgresoLeccionPatchRequest request);

    void eliminar(UUID id);
}
