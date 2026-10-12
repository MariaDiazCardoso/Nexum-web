package com.nexum.service;

import com.nexum.dto.ExamenRequest;
import com.nexum.dto.ExamenResponse;

import java.util.List;
import java.util.UUID;

public interface ExamenService {

    ExamenResponse crear(ExamenRequest request);

    List<ExamenResponse> listar(UUID leccionId);

    ExamenResponse obtenerPorId(UUID id);

    ExamenResponse actualizar(UUID id, ExamenRequest request);

    void eliminar(UUID id);
}
