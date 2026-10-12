package com.nexum.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para reemplazar completamente un usuario (PUT /api/usuarios/{id}).
 * Todos los campos son obligatorios.
 */
public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @Size(max = 255, message = "La URL del avatar no puede superar los 255 caracteres")
        String avatarUrl
) {}

