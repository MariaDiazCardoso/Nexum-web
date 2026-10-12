package com.nexum.service;

import com.nexum.dto.IaHistorialRequest;
import com.nexum.dto.IaHistorialResponse;

import java.util.List;
import java.util.UUID;

public interface IaHistorialService {
    IaHistorialResponse crear(IaHistorialRequest request);
    List<IaHistorialResponse> listar(UUID usuarioId, UUID cursoId, UUID leccionId);
    IaHistorialResponse obtenerPorId(Integer id);
    IaHistorialResponse actualizar(Integer id, IaHistorialRequest request);
    void eliminar(Integer id);
}
