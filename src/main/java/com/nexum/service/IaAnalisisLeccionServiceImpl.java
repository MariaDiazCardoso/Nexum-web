package com.nexum.service;

import com.nexum.dto.IaAnalisisLeccionRequest;
import com.nexum.dto.IaAnalisisLeccionResponse;
import com.nexum.entity.IaAnalisisLeccion;
import com.nexum.entity.Leccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.IaAnalisisLeccionRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IaAnalisisLeccionServiceImpl implements IaAnalisisLeccionService {

    private final IaAnalisisLeccionRepository iaAnalisisLeccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public IaAnalisisLeccionResponse crear(IaAnalisisLeccionRequest request) {
        accesoService.verificar(request.usuarioId());
        IaAnalisisLeccion analisis = new IaAnalisisLeccion();
        aplicar(analisis, request);
        return IaAnalisisLeccionResponse.desde(iaAnalisisLeccionRepository.save(analisis));
    }

    @Override
    public List<IaAnalisisLeccionResponse> listar(UUID usuarioId, UUID leccionId) {
        List<IaAnalisisLeccion> resultados;
        if (usuarioId != null && leccionId != null) {
            accesoService.verificar(usuarioId);
            resultados = iaAnalisisLeccionRepository.findByUsuario_IdAndLeccion_Id(usuarioId, leccionId);
        } else if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            resultados = iaAnalisisLeccionRepository.findByUsuario_Id(usuarioId);
        } else if (leccionId != null) {
            accesoService.exigirAdministrador();
            resultados = iaAnalisisLeccionRepository.findByLeccion_Id(leccionId);
        } else {
            accesoService.exigirAdministrador();
            resultados = iaAnalisisLeccionRepository.findAll();
        }
        return resultados.stream().map(IaAnalisisLeccionResponse::desde).toList();
    }

    @Override
    public IaAnalisisLeccionResponse obtenerPorId(Integer id) {
        IaAnalisisLeccion analisis = buscarOLanzar(id);
        accesoService.verificar(analisis.getUsuario().getId());
        return IaAnalisisLeccionResponse.desde(analisis);
    }

    @Override
    public IaAnalisisLeccionResponse actualizar(Integer id, IaAnalisisLeccionRequest request) {
        IaAnalisisLeccion analisis = buscarOLanzar(id);
        accesoService.verificar(analisis.getUsuario().getId());
        aplicar(analisis, request);
        return IaAnalisisLeccionResponse.desde(iaAnalisisLeccionRepository.save(analisis));
    }

    @Override
    public void eliminar(Integer id) {
        IaAnalisisLeccion analisis = buscarOLanzar(id);
        accesoService.verificar(analisis.getUsuario().getId());
        iaAnalisisLeccionRepository.delete(analisis);
    }

    private void aplicar(IaAnalisisLeccion analisis, IaAnalisisLeccionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        Leccion leccion = leccionRepository.findById(request.leccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + request.leccionId()));
        analisis.setUsuario(usuario);
        analisis.setLeccion(leccion);
        analisis.setResumen(request.resumen());
        analisis.setNivel(request.nivel());
        analisis.setDuracion(request.duracion());
        analisis.setObjetivos(request.objetivos());
        analisis.setConsejo(request.consejo());
        analisis.setSiguientePaso(request.siguientePaso());
        analisis.setProyecto(request.proyecto());
    }

    private IaAnalisisLeccion buscarOLanzar(Integer id) {
        return iaAnalisisLeccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Análisis IA no encontrado con id: " + id));
    }
}
