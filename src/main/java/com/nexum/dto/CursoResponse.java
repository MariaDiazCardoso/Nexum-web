package com.nexum.dto;

import com.nexum.entity.Curso;
import com.nexum.entity.EstadoCurso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para Curso.
 * Expone responsableId y categoriaIds en lugar de entidades anidadas.
 */
public record CursoResponse(
        UUID id,
        String titulo,
        String slug,
        String descripcion,
        UUID responsableId,
        BigDecimal precio,
        String nivel,
        EstadoCurso estado,
        BigDecimal promedioValoracion,
        LocalDateTime fechaCreacion,
        String recomendadoPara,
        String duracionEstimada,
        String ruta,
        String categoriaAcademica,
        List<UUID> categoriaIds
) {
    public static CursoResponse desde(Curso c) {
        List<UUID> catIds = c.getCategorias() == null
                ? List.of()
                : c.getCategorias().stream().map(cat -> cat.getId()).toList();

        return new CursoResponse(
                c.getId(),
                c.getTitulo(),
                c.getSlug(),
                c.getDescripcion(),
                c.getResponsable() != null ? c.getResponsable().getId() : null,
                c.getPrecio(),
                c.getNivel(),
                c.getEstado(),
                c.getPromedioValoracion(),
                c.getFechaCreacion(),
                c.getRecomendadoPara(),
                c.getDuracionEstimada(),
                c.getRuta(),
                c.getCategoriaAcademica(),
                catIds
        );
    }
}

