package com.nexum.service;

import com.nexum.dto.RecursoExternoCacheRequest;
import com.nexum.dto.RecursoExternoCacheResponse;
import com.nexum.entity.Leccion;
import com.nexum.entity.RecursoExternoCache;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.RecursoExternoCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecursoExternoCacheServiceImpl implements RecursoExternoCacheService {

    private final RecursoExternoCacheRepository recursoExternoCacheRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public RecursoExternoCacheResponse crear(RecursoExternoCacheRequest request) {
        accesoService.exigirAdministrador();
        RecursoExternoCache recurso = new RecursoExternoCache();
        aplicar(recurso, request);
        return RecursoExternoCacheResponse.desde(recursoExternoCacheRepository.save(recurso));
    }

    @Override
    public List<RecursoExternoCacheResponse> listar(UUID leccionId) {
        List<RecursoExternoCache> recursos = leccionId == null
                ? recursoExternoCacheRepository.findAll()
                : recursoExternoCacheRepository.findByLeccion_Id(leccionId);
        return recursos.stream().map(RecursoExternoCacheResponse::desde).toList();
    }

    @Override
    public RecursoExternoCacheResponse obtenerPorId(UUID id) {
        return RecursoExternoCacheResponse.desde(buscarOLanzar(id));
    }

    @Override
    public RecursoExternoCacheResponse actualizar(UUID id, RecursoExternoCacheRequest request) {
        accesoService.exigirAdministrador();
        RecursoExternoCache recurso = buscarOLanzar(id);
        aplicar(recurso, request);
        return RecursoExternoCacheResponse.desde(recursoExternoCacheRepository.save(recurso));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        recursoExternoCacheRepository.delete(buscarOLanzar(id));
    }

    private void aplicar(RecursoExternoCache recurso, RecursoExternoCacheRequest request) {
        recurso.setLeccion(obtenerLeccion(request.leccionId()));
        recurso.setTipo(request.tipo());
        recurso.setTitulo(request.titulo());
        recurso.setDescripcion(request.descripcion());
        recurso.setUrl(request.url());
        recurso.setThumbnail(request.thumbnail());
        recurso.setFuente(request.fuente());
        recurso.setMetadata(request.metadata());
    }

    private RecursoExternoCache buscarOLanzar(UUID id) {
        return recursoExternoCacheRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso externo no encontrado con id: " + id));
    }

    private Leccion obtenerLeccion(UUID id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + id));
    }
}
