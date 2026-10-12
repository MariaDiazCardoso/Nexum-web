package com.nexum.service;

import com.nexum.dto.OpcionRespuestaRequest;
import com.nexum.dto.PreguntaRequest;
import com.nexum.dto.PreguntaResponse;
import com.nexum.entity.Examen;
import com.nexum.entity.OpcionRespuesta;
import com.nexum.entity.Pregunta;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.ExamenRepository;
import com.nexum.repository.PreguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreguntaServiceImpl implements PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final ExamenRepository examenRepository;

    @Override
    public PreguntaResponse crear(PreguntaRequest request) {
        validarOpciones(request.opciones());
        Examen examen = obtenerExamen(request.examenId());

        Pregunta pregunta = Pregunta.builder()
                .examen(examen)
                .textoPregunta(request.textoPregunta())
                .explicacionRespuesta(request.explicacionRespuesta())
                .build();
        pregunta.reemplazarOpciones(mapearOpciones(request.opciones()));

        return PreguntaResponse.desde(preguntaRepository.save(pregunta));
    }

    @Override
    public List<PreguntaResponse> listar(UUID examenId) {
        List<Pregunta> preguntas = examenId == null
                ? preguntaRepository.findAll()
                : preguntaRepository.findByExamen_Id(examenId);

        return preguntas.stream()
                .map(PreguntaResponse::desde)
                .toList();
    }

    @Override
    public PreguntaResponse obtenerPorId(UUID id) {
        return PreguntaResponse.desde(buscarOLanzar(id));
    }

    @Override
    @Transactional
    public PreguntaResponse actualizar(UUID id, PreguntaRequest request) {
        validarOpciones(request.opciones());

        Pregunta pregunta = buscarOLanzar(id);
        Examen examen = obtenerExamen(request.examenId());

        pregunta.setExamen(examen);
        pregunta.setTextoPregunta(request.textoPregunta());
        pregunta.setExplicacionRespuesta(request.explicacionRespuesta());
        pregunta.reemplazarOpciones(mapearOpciones(request.opciones()));

        return PreguntaResponse.desde(preguntaRepository.save(pregunta));
    }

    @Override
    public void eliminar(UUID id) {
        Pregunta pregunta = buscarOLanzar(id);
        preguntaRepository.delete(pregunta);
    }

    private Pregunta buscarOLanzar(UUID id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Pregunta no encontrada con id: " + id));
    }

    private Examen obtenerExamen(UUID examenId) {
        return examenRepository.findById(examenId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Examen no encontrado con id: " + examenId));
    }

    private void validarOpciones(List<OpcionRespuestaRequest> opciones) {
        if (opciones == null || opciones.size() < 2) {
            throw new SolicitudInvalidaException("Una pregunta debe tener al menos 2 opciones");
        }
        boolean tieneCorrecta = opciones.stream()
                .anyMatch(opcion -> Boolean.TRUE.equals(opcion.esCorrecta()));
        if (!tieneCorrecta) {
            throw new SolicitudInvalidaException("Una pregunta debe tener al menos 1 opción correcta");
        }
    }

    private List<OpcionRespuesta> mapearOpciones(List<OpcionRespuestaRequest> opciones) {
        return opciones.stream()
                .map(opcion -> OpcionRespuesta.builder()
                        .textoOpcion(opcion.textoOpcion())
                        .esCorrecta(opcion.esCorrecta())
                        .build())
                .toList();
    }
}
