package com.nexum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO para crear o reemplazar una Categoria (POST y PUT). */
public record CategoriaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La URL del icono no puede superar los 255 caracteres")
        String iconoUrl
) {}

