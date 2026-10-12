package com.nexum.service;

import com.nexum.dto.CertificadoRequest;
import com.nexum.dto.CertificadoResponse;

import java.util.List;
import java.util.UUID;

public interface CertificadoService {

    CertificadoResponse crear(CertificadoRequest request);

    List<CertificadoResponse> listar(UUID usuarioId, String codigo);

    CertificadoResponse obtenerPorId(UUID id);

    CertificadoResponse actualizar(UUID id, CertificadoRequest request);

    void eliminar(UUID id);
}
