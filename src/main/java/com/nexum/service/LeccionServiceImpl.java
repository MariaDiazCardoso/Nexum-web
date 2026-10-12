package com.nexum.service;

import com.nexum.dto.LeccionRequest;
import com.nexum.dto.LeccionResponse;
import com.nexum.entity.Leccion;
import com.nexum.entity.Modulo;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.ActividadRepository;
import com.nexum.repository.ExamenRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.ModuloRepository;
import com.nexum.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeccionServiceImpl implements LeccionService {

    private final LeccionRepository leccionRepository;
    private final ModuloRepository moduloRepository;
    private final ActividadRepository actividadRepository;
    private final ExamenRepository examenRepository;

    @Override
    public LeccionResponse crear(LeccionRequest request) {
        Modulo modulo = obtenerModulo(request.moduloId());
        validarOrdenDisponible(request.moduloId(), request.orden());

        Leccion leccion = Leccion.builder()
                .modulo(modulo)
                .titulo(request.titulo())
                .tipo(request.tipo())
                .orden(request.orden())
                .contenidoTexto(request.contenidoTexto())
                .puntosOtorgados(request.puntosOtorgados())
                .videoUrl(request.videoUrl())
                .xpOtorgada(request.xpOtorgada())
                .slug(resolverSlug(request.slug(), request.titulo()))
                .retoPractico(request.retoPractico())
                .dificultad(request.dificultad())
                .duracionEstimada(request.duracionEstimada())
                .duracionMinima(request.duracionMinima())
                .imagenUrl(request.imagenUrl())
                .build();

        return LeccionResponse.desde(leccionRepository.save(leccion));
    }

    @Override
    public List<LeccionResponse> listar(UUID moduloId) {
        List<Leccion> lecciones = moduloId == null
                ? leccionRepository.findAllByOrderByOrdenAsc()
                : leccionRepository.findByModulo_IdOrderByOrdenAsc(moduloId);

        return lecciones.stream()
                .map(LeccionResponse::desde)
                .toList();
    }

    @Override
    public LeccionResponse obtenerPorId(UUID id) {
        return LeccionResponse.desde(buscarOLanzar(id));
    }

    @Override
    public LeccionResponse actualizar(UUID id, LeccionRequest request) {
        Leccion leccion = buscarOLanzar(id);
        Modulo modulo = obtenerModulo(request.moduloId());
        validarOrdenDisponibleParaOtraLeccion(request.moduloId(), request.orden(), id);

        leccion.setModulo(modulo);
        leccion.setTitulo(request.titulo());
        leccion.setTipo(request.tipo());
        leccion.setOrden(request.orden());
        leccion.setContenidoTexto(request.contenidoTexto());
        leccion.setPuntosOtorgados(request.puntosOtorgados());
        leccion.setVideoUrl(request.videoUrl());
        leccion.setXpOtorgada(request.xpOtorgada());
        leccion.setSlug(resolverSlug(request.slug(), request.titulo()));
        leccion.setRetoPractico(request.retoPractico());
        leccion.setDificultad(request.dificultad());
        leccion.setDuracionEstimada(request.duracionEstimada());
        leccion.setDuracionMinima(request.duracionMinima());
        leccion.setImagenUrl(request.imagenUrl());

        return LeccionResponse.desde(leccionRepository.save(leccion));
    }

    @Override
    public void eliminar(UUID id) {
        Leccion leccion = buscarOLanzar(id);
        if (actividadRepository.existsByLeccion_Id(id) || examenRepository.existsByLeccion_Id(id)) {
            throw new ConflictoException("No se puede eliminar la lección porque tiene dependientes asociados");
        }
        leccionRepository.delete(leccion);
    }

    private Leccion buscarOLanzar(UUID id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lección no encontrada con id: " + id));
    }

    private Modulo obtenerModulo(UUID moduloId) {
        return moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Módulo no encontrado con id: " + moduloId));
    }

    private void validarOrdenDisponible(UUID moduloId, Integer orden) {
        if (leccionRepository.existsByModulo_IdAndOrden(moduloId, orden)) {
            throw new ConflictoException("Ya existe una lección con el orden " + orden + " en el módulo");
        }
    }

    private void validarOrdenDisponibleParaOtraLeccion(UUID moduloId, Integer orden, UUID id) {
        if (leccionRepository.existsByModulo_IdAndOrdenAndIdNot(moduloId, orden, id)) {
            throw new ConflictoException("Ya existe otra lección con el orden " + orden + " en el módulo");
        }
    }

    private static String resolverSlug(String slug, String titulo) {
        String origen = slug == null || slug.isBlank() ? titulo : slug;
        return SlugUtil.generar(origen);
    }
}
