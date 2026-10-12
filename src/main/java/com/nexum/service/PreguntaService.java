package com.nexum.service;

import com.nexum.dto.PreguntaRequest;
import com.nexum.dto.PreguntaResponse;

import java.util.List;
import java.util.UUID;

public interface PreguntaService {

    PreguntaResponse crear(PreguntaRequest request);

    List<PreguntaResponse> listar(UUID examenId);

    PreguntaResponse obtenerPorId(UUID id);

    PreguntaResponse actualizar(UUID id, PreguntaRequest request);

    void eliminar(UUID id);
}
