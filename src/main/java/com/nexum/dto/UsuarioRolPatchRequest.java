package com.nexum.dto;

import com.nexum.entity.RolUsuario;
import jakarta.validation.constraints.NotNull;

public record UsuarioRolPatchRequest(

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol
) {}
