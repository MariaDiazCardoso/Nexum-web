package com.nexum.dto;

import com.nexum.entity.EstadoCurso;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO para crear o reemplazar un Curso (POST y PUT).
 * El slug es opcional: si no llega, el servicio lo genera con SlugUtil.
 * categoriaIds puede ser vacío pero no null.
 */
public record CursoRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String titulo,

        @Size(max = 255, message = "El slug no puede superar los 255 caracteres")
        String slug,

        String descripcion,

        @NotNull(message = "El responsableId es obligatorio")
        UUID responsableId,

        @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
        BigDecimal precio,

        @Size(max = 20, message = "El nivel no puede superar los 20 caracteres")
        String nivel,

        EstadoCurso estado,

        String recomendadoPara,

        @Size(max = 50, message = "La duración estimada no puede superar los 50 caracteres")
        String duracionEstimada,

        @Size(max = 50, message = "La ruta no puede superar los 50 caracteres")
        String ruta,

        @Size(max = 80, message = "La categoría académica no puede superar los 80 caracteres")
        String categoriaAcademica,

        @NotNull(message = "La lista de categorías es obligatoria (puede estar vacía)")
        List<UUID> categoriaIds
) {}

