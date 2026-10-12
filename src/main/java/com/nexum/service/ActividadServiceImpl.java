package com.nexum.service;

import com.nexum.dto.ActividadRequest;
import com.nexum.dto.ActividadResponse;
import com.nexum.entity.Actividad;
import com.nexum.entity.Leccion;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.ActividadRepository;
import com.nexum.repository.LeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;
    private final LeccionRepository leccionRepository;

    @Override
    public ActividadResponse crear(ActividadRequest request) {
        Leccion leccion = obtenerLeccion(request.leccionId());

        Actividad actividad = Actividad.builder()
                .leccion(leccion)
                .titulo(request.titulo())
                .instrucciones(request.instrucciones())
                .tipo(request.tipo())
                .build();

        return ActividadResponse.desde(actividadRepository.save(actividad));
    }

    @Override
    public List<ActividadResponse> listar(UUID leccionId) {
        List<Actividad> actividades = leccionId == null
                ? actividadRepository.findAll()
                : actividadRepository.findByLeccion_Id(leccionId);

        return actividades.stream()
                .map(ActividadResponse::desde)
                .toList();
    }

    @Override
    public ActividadResponse obtenerPorId(UUID id) {
        return ActividadResponse.desde(buscarOLanzar(id));
    }

    @Override
    public ActividadResponse actualizar(UUID id, ActividadRequest request) {
        Actividad actividad = buscarOLanzar(id);
        Leccion leccion = obtenerLeccion(request.leccionId());

        actividad.setLeccion(leccion);
        actividad.setTitulo(request.titulo());
        actividad.setInstrucciones(request.instrucciones());
        actividad.setTipo(request.tipo());

        return ActividadResponse.desde(actividadRepository.save(actividad));
    }

    @Override
    public void eliminar(UUID id) {
        Actividad actividad = buscarOLanzar(id);
        actividadRepository.delete(actividad);
    }

    private Actividad buscarOLanzar(UUID id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Actividad no encontrada con id: " + id));
    }

    private Leccion obtenerLeccion(UUID leccionId) {
        return leccionRepository.findById(leccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lección no encontrada con id: " + leccionId));
    }
}
