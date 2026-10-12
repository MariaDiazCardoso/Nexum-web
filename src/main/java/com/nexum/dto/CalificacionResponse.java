package com.nexum.dto;

import com.nexum.entity.Calificacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CalificacionResponse(
        UUID id,
        UUID usuarioId,
        UUID cursoId,
        UUID leccionId,
        String tipo,
        String titulo,
        BigDecimal nota,
        BigDecimal notaMaxima,
        BigDecimal peso,
        String comentario,
        LocalDateTime fechaRegistro
) {
    public static CalificacionResponse desde(Calificacion calificacion) {
        return new CalificacionResponse(
                calificacion.getId(),
                calificacion.getUsuario() != null ? calificacion.getUsuario().getId() : null,
                calificacion.getCurso() != null ? calificacion.getCurso().getId() : null,
                calificacion.getLeccion() != null ? calificacion.getLeccion().getId() : null,
                calificacion.getTipo(),
                calificacion.getTitulo(),
                calificacion.getNota(),
                calificacion.getNotaMaxima(),
                calificacion.getPeso(),
                calificacion.getComentario(),
                calificacion.getFechaRegistro()
        );
    }
}
