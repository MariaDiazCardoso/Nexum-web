package com.nexum.service;

import com.nexum.dto.InscripcionPatchRequest;
import com.nexum.dto.InscripcionRequest;
import com.nexum.dto.InscripcionResponse;

import java.util.List;
import java.util.UUID;

public interface InscripcionService {

    InscripcionResponse crear(InscripcionRequest request);

    List<InscripcionResponse> listar(UUID usuarioId, UUID cursoId);

    List<InscripcionResponse> listarPorEmail(String email);

    InscripcionResponse obtenerPorId(UUID id);

    InscripcionResponse actualizarEstado(UUID id, InscripcionPatchRequest request);

    void eliminar(UUID id);
}
