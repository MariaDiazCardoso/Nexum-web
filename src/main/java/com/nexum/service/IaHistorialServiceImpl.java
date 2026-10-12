package com.nexum.service;

import com.nexum.dto.IaHistorialRequest;
import com.nexum.dto.IaHistorialResponse;
import com.nexum.entity.Curso;
import com.nexum.entity.IaHistorial;
import com.nexum.entity.Leccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.IaHistorialRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IaHistorialServiceImpl implements IaHistorialService {

    private final IaHistorialRepository iaHistorialRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public IaHistorialResponse crear(IaHistorialRequest request) {
        accesoService.verificar(request.usuarioId());
        IaHistorial historial = new IaHistorial();
        aplicar(historial, request);
        return IaHistorialResponse.desde(iaHistorialRepository.save(historial));
    }

    @Override
    public List<IaHistorialResponse> listar(UUID usuarioId, UUID cursoId, UUID leccionId) {
        List<IaHistorial> resultados;
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
        } else {
            accesoService.exigirAdministrador();
        }

        if (usuarioId != null && cursoId != null && leccionId != null) {
            resultados = iaHistorialRepository.findByUsuario_IdAndCurso_IdAndLeccion_Id(usuarioId, cursoId, leccionId);
        } else if (usuarioId != null && cursoId != null) {
            resultados = iaHistorialRepository.findByUsuario_IdAndCurso_Id(usuarioId, cursoId);
        } else if (usuarioId != null && leccionId != null) {
            resultados = iaHistorialRepository.findByUsuario_IdAndLeccion_Id(usuarioId, leccionId);
        } else if (cursoId != null && leccionId != null) {
            resultados = iaHistorialRepository.findByCurso_IdAndLeccion_Id(cursoId, leccionId);
        } else if (usuarioId != null) {
            resultados = iaHistorialRepository.findByUsuario_Id(usuarioId);
        } else if (cursoId != null) {
            resultados = iaHistorialRepository.findByCurso_Id(cursoId);
        } else if (leccionId != null) {
            resultados = iaHistorialRepository.findByLeccion_Id(leccionId);
        } else {
            resultados = iaHistorialRepository.findAll();
        }
        return resultados.stream().map(IaHistorialResponse::desde).toList();
    }

    @Override
    public IaHistorialResponse obtenerPorId(Integer id) {
        IaHistorial historial = buscarOLanzar(id);
        accesoService.verificar(historial.getUsuario().getId());
        return IaHistorialResponse.desde(historial);
    }

    @Override
    public IaHistorialResponse actualizar(Integer id, IaHistorialRequest request) {
        IaHistorial historial = buscarOLanzar(id);
        accesoService.verificar(historial.getUsuario().getId());
        aplicar(historial, request);
        return IaHistorialResponse.desde(iaHistorialRepository.save(historial));
    }

    @Override
    public void eliminar(Integer id) {
        IaHistorial historial = buscarOLanzar(id);
        accesoService.verificar(historial.getUsuario().getId());
        iaHistorialRepository.delete(historial);
    }

    private void aplicar(IaHistorial historial, IaHistorialRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        Curso curso = request.cursoId() == null ? null : cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + request.cursoId()));
        Leccion leccion = request.leccionId() == null ? null : leccionRepository.findById(request.leccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + request.leccionId()));
        historial.setUsuario(usuario);
        historial.setCurso(curso);
        historial.setLeccion(leccion);
        historial.setRol(request.rol());
        historial.setMensaje(request.mensaje());
    }

    private IaHistorial buscarOLanzar(Integer id) {
        return iaHistorialRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Historial IA no encontrado con id: " + id));
    }
}
