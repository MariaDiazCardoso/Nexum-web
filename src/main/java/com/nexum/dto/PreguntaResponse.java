package com.nexum.dto;

import com.nexum.entity.Pregunta;

import java.util.List;
import java.util.UUID;

public record PreguntaResponse(
        UUID id,
        UUID examenId,
        String textoPregunta,
        String explicacionRespuesta,
        List<OpcionRespuestaResponse> opciones
) {
    public static PreguntaResponse desde(Pregunta pregunta) {
        List<OpcionRespuestaResponse> opciones = pregunta.getOpciones() == null
                ? List.of()
                : pregunta.getOpciones().stream()
                        .map(OpcionRespuestaResponse::desde)
                        .toList();

        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getExamen() != null ? pregunta.getExamen().getId() : null,
                pregunta.getTextoPregunta(),
                pregunta.getExplicacionRespuesta(),
                opciones
        );
    }

    public static PreguntaResponse desde(Pregunta pregunta, boolean mostrarCorrectas) {
        List<OpcionRespuestaResponse> opciones = pregunta.getOpciones() == null
                ? List.of()
                : pregunta.getOpciones().stream()
                        .map(opcion -> OpcionRespuestaResponse.desde(opcion, mostrarCorrectas))
                        .toList();

        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getExamen() != null ? pregunta.getExamen().getId() : null,
                pregunta.getTextoPregunta(),
                pregunta.getExplicacionRespuesta(),
                opciones
        );
    }
}
