package com.nexum.service;

import com.nexum.dto.RecursoAdjuntoRequest;
import com.nexum.dto.RecursoAdjuntoResponse;
import com.nexum.entity.Leccion;
import com.nexum.entity.RecursoAdjunto;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.RecursoAdjuntoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecursoAdjuntoServiceImpl implements RecursoAdjuntoService {

    private final RecursoAdjuntoRepository recursoAdjuntoRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public RecursoAdjuntoResponse crear(RecursoAdjuntoRequest request) {
        accesoService.exigirAdministrador();
        RecursoAdjunto recurso = new RecursoAdjunto();
        aplicar(recurso, request);
        return RecursoAdjuntoResponse.desde(recursoAdjuntoRepository.save(recurso));
    }

    @Override
    public List<RecursoAdjuntoResponse> listar(UUID leccionId) {
        List<RecursoAdjunto> recursos = leccionId == null
                ? recursoAdjuntoRepository.findAll()
                : recursoAdjuntoRepository.findByLeccion_Id(leccionId);
        return recursos.stream().map(RecursoAdjuntoResponse::desde).toList();
    }

    @Override
    public RecursoAdjuntoResponse obtenerPorId(UUID id) {
        return RecursoAdjuntoResponse.desde(buscarOLanzar(id));
    }

    @Override
    public RecursoAdjuntoResponse actualizar(UUID id, RecursoAdjuntoRequest request) {
        accesoService.exigirAdministrador();
        RecursoAdjunto recurso = buscarOLanzar(id);
        aplicar(recurso, request);
        return RecursoAdjuntoResponse.desde(recursoAdjuntoRepository.save(recurso));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        recursoAdjuntoRepository.delete(buscarOLanzar(id));
    }

    private void aplicar(RecursoAdjunto recurso, RecursoAdjuntoRequest request) {
        recurso.setLeccion(obtenerLeccion(request.leccionId()));
        recurso.setNombre(request.nombre());
        recurso.setUrlArchivo(request.urlArchivo());
        recurso.setTipoArchivo(request.tipoArchivo());
    }

    private RecursoAdjunto buscarOLanzar(UUID id) {
        return recursoAdjuntoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso adjunto no encontrado con id: " + id));
    }

    private Leccion obtenerLeccion(UUID id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + id));
    }
}
