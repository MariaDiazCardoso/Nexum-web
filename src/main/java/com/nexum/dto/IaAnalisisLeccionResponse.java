package com.nexum.dto;

import com.nexum.entity.IaAnalisisLeccion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record IaAnalisisLeccionResponse(
        Integer id,
        UUID usuarioId,
        UUID leccionId,
        String resumen,
        String nivel,
        String duracion,
        List<String> objetivos,
        String consejo,
        String siguientePaso,
        Map<String, Object> proyecto,
        LocalDateTime fechaCreacion
) {
    public static IaAnalisisLeccionResponse desde(IaAnalisisLeccion analisis) {
        return new IaAnalisisLeccionResponse(
                analisis.getId(),
                analisis.getUsuario() != null ? analisis.getUsuario().getId() : null,
                analisis.getLeccion() != null ? analisis.getLeccion().getId() : null,
                analisis.getResumen(),
                analisis.getNivel(),
                analisis.getDuracion(),
                analisis.getObjetivos(),
                analisis.getConsejo(),
                analisis.getSiguientePaso(),
                analisis.getProyecto(),
                analisis.getFechaCreacion()
        );
    }
}
