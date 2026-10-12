package com.nexum.service;

import com.nexum.dto.ActividadRequest;
import com.nexum.dto.ActividadResponse;

import java.util.List;
import java.util.UUID;

public interface ActividadService {

    ActividadResponse crear(ActividadRequest request);

    List<ActividadResponse> listar(UUID leccionId);

    ActividadResponse obtenerPorId(UUID id);

    ActividadResponse actualizar(UUID id, ActividadRequest request);

    void eliminar(UUID id);
}
