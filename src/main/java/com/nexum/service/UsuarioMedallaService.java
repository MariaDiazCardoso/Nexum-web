package com.nexum.service;

import com.nexum.dto.UsuarioMedallaRequest;
import com.nexum.dto.UsuarioMedallaResponse;

import java.util.List;
import java.util.UUID;

public interface UsuarioMedallaService {
    UsuarioMedallaResponse asignar(UUID usuarioId, UsuarioMedallaRequest request);
    List<UsuarioMedallaResponse> listar(UUID usuarioId);
    void eliminar(UUID usuarioId, UUID medallaId);
}
