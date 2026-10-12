package com.nexum.service;

import com.nexum.dto.*;
import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de Usuario (T03).
 *
 * <p>Reglas de negocio:
 * <ul>
 *   <li>Email se normaliza: trim + toLowerCase.</li>
 *   <li>Email duplicado → {@link ConflictoException}.</li>
 *   <li>En actualización se excluye al propio usuario del chequeo de email.</li>
 *   <li>La contraseña se almacena con BCrypt.</li>
 *   <li>El registro fuerza {@code rol = USUARIO}.</li>
 *   <li>{@code rol} y {@code emailVerificado} no se modifican por PUT ni PATCH.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Registro ──────────────────────────────────────────────────────────────

    @Override
    public UsuarioResponse registrar(UsuarioRegistroRequest request) {
        String email = normalizar(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictoException("Ya existe un usuario con el email: " + email);
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(RolUsuario.USUARIO)   // siempre forzado (T03)
                .build();

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::desde)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(UUID id) {
        return UsuarioResponse.desde(buscarOLanzar(id));
    }

    // ── Actualización completa (PUT) ──────────────────────────────────────────

    @Override
    public UsuarioResponse actualizar(UUID id, UsuarioRequest request) {
        Usuario usuario = buscarOLanzar(id);
        String email = normalizar(request.email());

        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflictoException("El email ya está en uso por otro usuario: " + email);
        }

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(email);
        usuario.setAvatarUrl(request.avatarUrl());
        // rol y emailVerificado no se modifican (T03)

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    // ── Actualización parcial (PATCH) ─────────────────────────────────────────

    @Override
    public UsuarioResponse actualizarParcial(UUID id, UsuarioPatchRequest request) {
        Usuario usuario = buscarOLanzar(id);

        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }
        if (request.apellido() != null) {
            usuario.setApellido(request.apellido());
        }
        if (request.email() != null) {
            String email = normalizar(request.email());
            if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
                throw new ConflictoException("El email ya está en uso por otro usuario: " + email);
            }
            usuario.setEmail(email);
        }
        if (request.avatarUrl() != null) {
            usuario.setAvatarUrl(request.avatarUrl());
        }
        // rol y emailVerificado no se modifican (T03)

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    // ── Eliminación ───────────────────────────────────────────────────────────

    @Override
    public void eliminar(UUID id) {
        Usuario usuario = buscarOLanzar(id);
        usuarioRepository.delete(usuario);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Usuario buscarOLanzar(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id));
    }

    /** Normaliza el email: trim + minúsculas. ⚠ Supuesto D aplicado. */
    private static String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

