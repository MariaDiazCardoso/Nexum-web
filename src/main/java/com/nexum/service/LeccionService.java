package com.nexum.service;

import com.nexum.dto.LeccionRequest;
import com.nexum.dto.LeccionResponse;

import java.util.List;
import java.util.UUID;

public interface LeccionService {

    LeccionResponse crear(LeccionRequest request);

    List<LeccionResponse> listar(UUID moduloId);

    LeccionResponse obtenerPorId(UUID id);

    LeccionResponse actualizar(UUID id, LeccionRequest request);

    void eliminar(UUID id);
}
