package com.nexum.service;

import com.nexum.dto.MedallaRequest;
import com.nexum.dto.MedallaResponse;

import java.util.List;
import java.util.UUID;

public interface MedallaService {
    MedallaResponse crear(MedallaRequest request);
    List<MedallaResponse> listar();
    MedallaResponse obtenerPorId(UUID id);
    MedallaResponse actualizar(UUID id, MedallaRequest request);
    void eliminar(UUID id);
}
