package com.nexum.service;

import com.nexum.dto.CategoriaRequest;
import com.nexum.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;

/** Contrato de negocio para el módulo Categoria (T04). */
public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    List<CategoriaResponse> listar();

    CategoriaResponse obtenerPorId(UUID id);

    CategoriaResponse actualizar(UUID id, CategoriaRequest request);

    void eliminar(UUID id);
}

