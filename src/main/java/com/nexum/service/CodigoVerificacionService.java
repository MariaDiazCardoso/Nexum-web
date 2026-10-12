package com.nexum.service;

import com.nexum.dto.CodigoVerificacionRequest;
import com.nexum.dto.CodigoVerificacionResponse;
import com.nexum.dto.CodigoVerificacionValidacionRequest;

import java.util.List;
import java.util.UUID;

public interface CodigoVerificacionService {
    CodigoVerificacionResponse generar(CodigoVerificacionRequest request);
    CodigoVerificacionResponse validar(UUID id, CodigoVerificacionValidacionRequest request);
    List<CodigoVerificacionResponse> listar(UUID usuarioId);
}
