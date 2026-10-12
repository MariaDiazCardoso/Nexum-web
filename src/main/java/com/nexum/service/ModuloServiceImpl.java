package com.nexum.service;

import com.nexum.dto.ModuloPatchRequest;
import com.nexum.dto.ModuloRequest;
import com.nexum.dto.ModuloResponse;
import com.nexum.entity.Curso;
import com.nexum.entity.Modulo;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.ModuloRepository;
import com.nexum.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuloServiceImpl implements ModuloService {

    private final ModuloRepository moduloRepository;
    private final CursoRepository cursoRepository;

    @Override
    public ModuloResponse crear(ModuloRequest request) {
        Curso curso = obtenerCurso(request.cursoId());
        validarOrdenDisponible(request.cursoId(), request.orden());

        Modulo modulo = Modulo.builder()
                .curso(curso)
                .titulo(request.titulo())
                .orden(request.orden())
                .descripcion(request.descripcion())
                .slug(resolverSlug(request.slug(), request.titulo()))
                .build();

        return ModuloResponse.desde(moduloRepository.save(modulo));
    }

    @Override
    public List<ModuloResponse> listar(UUID cursoId) {
        List<Modulo> modulos = cursoId == null
                ? moduloRepository.findAllByOrderByOrdenAsc()
                : moduloRepository.findByCurso_IdOrderByOrdenAsc(cursoId);

        return modulos.stream()
                .map(ModuloResponse::desde)
                .toList();
    }

    @Override
    public ModuloResponse obtenerPorId(UUID id) {
        return ModuloResponse.desde(buscarOLanzar(id));
    }

    @Override
    public ModuloResponse actualizar(UUID id, ModuloRequest request) {
        Modulo modulo = buscarOLanzar(id);
        Curso curso = obtenerCurso(request.cursoId());
        validarOrdenDisponibleParaOtroModulo(request.cursoId(), request.orden(), id);

        modulo.setCurso(curso);
        modulo.setTitulo(request.titulo());
        modulo.setOrden(request.orden());
        modulo.setDescripcion(request.descripcion());
        modulo.setSlug(resolverSlug(request.slug(), request.titulo()));

        return ModuloResponse.desde(moduloRepository.save(modulo));
    }

    @Override
    public ModuloResponse actualizarParcial(UUID id, ModuloPatchRequest request) {
        Modulo modulo = buscarOLanzar(id);

        UUID cursoIdFinal = modulo.getCurso().getId();
        Integer ordenFinal = modulo.getOrden();

        if (request.cursoId() != null) {
            modulo.setCurso(obtenerCurso(request.cursoId()));
            cursoIdFinal = request.cursoId();
        }
        if (request.orden() != null) {
            ordenFinal = request.orden();
        }
        if (request.cursoId() != null || request.orden() != null) {
            validarOrdenDisponibleParaOtroModulo(cursoIdFinal, ordenFinal, id);
            modulo.setOrden(ordenFinal);
        }
        if (request.titulo() != null) {
            modulo.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            modulo.setDescripcion(request.descripcion());
        }
        if (request.slug() != null) {
            modulo.setSlug(resolverSlug(request.slug(), modulo.getTitulo()));
        }

        return ModuloResponse.desde(moduloRepository.save(modulo));
    }

    @Override
    public void eliminar(UUID id) {
        Modulo modulo = buscarOLanzar(id);
        if (moduloRepository.contarLeccionesPorModulo(id) > 0) {
            throw new ConflictoException("No se puede eliminar el módulo porque tiene lecciones asociadas");
        }
        moduloRepository.delete(modulo);
    }

    private Modulo buscarOLanzar(UUID id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Módulo no encontrado con id: " + id));
    }

    private Curso obtenerCurso(UUID cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con id: " + cursoId));
    }

    private void validarOrdenDisponible(UUID cursoId, Integer orden) {
        if (moduloRepository.existsByCurso_IdAndOrden(cursoId, orden)) {
            throw new ConflictoException("Ya existe un módulo con el orden " + orden + " en el curso");
        }
    }

    private void validarOrdenDisponibleParaOtroModulo(UUID cursoId, Integer orden, UUID id) {
        if (moduloRepository.existsByCurso_IdAndOrdenAndIdNot(cursoId, orden, id)) {
            throw new ConflictoException("Ya existe otro módulo con el orden " + orden + " en el curso");
        }
    }

    private static String resolverSlug(String slug, String titulo) {
        String origen = slug == null || slug.isBlank() ? titulo : slug;
        return SlugUtil.generar(origen);
    }
}
