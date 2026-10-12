package com.nexum.service;

import com.nexum.dto.ModuloPatchRequest;
import com.nexum.dto.ModuloRequest;
import com.nexum.dto.ModuloResponse;

import java.util.List;
import java.util.UUID;

public interface ModuloService {

    ModuloResponse crear(ModuloRequest request);

    List<ModuloResponse> listar(UUID cursoId);

    ModuloResponse obtenerPorId(UUID id);

    ModuloResponse actualizar(UUID id, ModuloRequest request);

    ModuloResponse actualizarParcial(UUID id, ModuloPatchRequest request);

    void eliminar(UUID id);
}
