package com.nexum.service;

import com.nexum.dto.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrato de negocio para el módulo Usuario (T03).
 * Los controladores y las pruebas dependen únicamente de esta interfaz.
 */
public interface UsuarioService {

    UsuarioResponse registrar(UsuarioRegistroRequest request);

    List<UsuarioResponse> listar();

    UsuarioResponse obtenerPorId(UUID id);

    UsuarioResponse actualizar(UUID id, UsuarioRequest request);

    UsuarioResponse actualizarParcial(UUID id, UsuarioPatchRequest request);

    UsuarioResponse actualizarRol(UUID id, UsuarioRolPatchRequest request);

    UsuarioResponse actualizarEmailVerificado(UUID id, UsuarioEmailVerificadoPatchRequest request);

    void eliminar(UUID id);
}
