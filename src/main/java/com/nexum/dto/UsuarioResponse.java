package com.nexum.dto;

import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para Usuario.
 * Nunca expone passwordHash (T03).
 * El mapeo se hace con el método estático {@code desde(Usuario)}.
 */
public record UsuarioResponse(
        UUID id,
        String nombre,
        String apellido,
        String email,
        RolUsuario rol,
        Integer puntosXp,
        String avatarUrl,
        LocalDateTime fechaRegistro,
        LocalDateTime ultimaConexion,
        Boolean emailVerificado
) {
    /** Mapeo manual desde la entidad. No usa MapStruct (convención del proyecto). */
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getRol(),
                u.getPuntosXp(),
                u.getAvatarUrl(),
                u.getFechaRegistro(),
                u.getUltimaConexion(),
                u.getEmailVerificado()
        );
    }
}

