package com.nexum.service;

import com.nexum.dto.CalificacionRequest;
import com.nexum.dto.CalificacionResponse;
import com.nexum.entity.Calificacion;
import com.nexum.entity.Curso;
import com.nexum.entity.Leccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.CalificacionRepository;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public CalificacionResponse crear(CalificacionRequest request) {
        accesoService.exigirAdministrador();
        validarNotas(request.nota(), request.notaMaxima());

        Calificacion calificacion = construirDesdeRequest(new Calificacion(), request);
        return CalificacionResponse.desde(calificacionRepository.save(calificacion));
    }

    @Override
    public List<CalificacionResponse> listar(UUID usuarioId, UUID cursoId) {
        List<Calificacion> calificaciones;

        if (usuarioId != null && cursoId != null) {
            accesoService.verificar(usuarioId);
            calificaciones = calificacionRepository.findByUsuario_IdAndCurso_Id(usuarioId, cursoId);
        } else if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            calificaciones = calificacionRepository.findByUsuario_Id(usuarioId);
        } else if (cursoId != null) {
            accesoService.exigirAdministrador();
            calificaciones = calificacionRepository.findByCurso_Id(cursoId);
        } else {
            accesoService.exigirAdministrador();
            calificaciones = calificacionRepository.findAll();
        }

        return calificaciones.stream()
                .map(CalificacionResponse::desde)
                .toList();
    }

    @Override
    public CalificacionResponse obtenerPorId(UUID id) {
        Calificacion calificacion = buscarOLanzar(id);
        accesoService.verificar(calificacion.getUsuario().getId());
        return CalificacionResponse.desde(calificacion);
    }

    @Override
    public CalificacionResponse actualizar(UUID id, CalificacionRequest request) {
        accesoService.exigirAdministrador();
        validarNotas(request.nota(), request.notaMaxima());

        Calificacion calificacion = construirDesdeRequest(buscarOLanzar(id), request);
        return CalificacionResponse.desde(calificacionRepository.save(calificacion));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        Calificacion calificacion = buscarOLanzar(id);
        calificacionRepository.delete(calificacion);
    }

    private Calificacion construirDesdeRequest(Calificacion calificacion, CalificacionRequest request) {
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Curso curso = obtenerCurso(request.cursoId());
        Leccion leccion = request.leccionId() != null ? obtenerLeccion(request.leccionId()) : null;

        calificacion.setUsuario(usuario);
        calificacion.setCurso(curso);
        calificacion.setLeccion(leccion);
        calificacion.setTipo(request.tipo());
        calificacion.setTitulo(request.titulo());
        calificacion.setNota(request.nota());
        calificacion.setNotaMaxima(request.notaMaxima());
        calificacion.setPeso(request.peso());
        calificacion.setComentario(request.comentario());
        return calificacion;
    }

    private void validarNotas(BigDecimal nota, BigDecimal notaMaxima) {
        if (notaMaxima == null || notaMaxima.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolicitudInvalidaException("La nota máxima debe ser mayor a 0");
        }
        if (nota == null || nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(notaMaxima) > 0) {
            throw new SolicitudInvalidaException("La nota debe estar entre 0 y la nota máxima");
        }
    }

    private Calificacion buscarOLanzar(UUID id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Calificación no encontrada con id: " + id));
    }

    private Usuario obtenerUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + usuarioId));
    }

    private Curso obtenerCurso(UUID cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con id: " + cursoId));
    }

    private Leccion obtenerLeccion(UUID leccionId) {
        return leccionRepository.findById(leccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lección no encontrada con id: " + leccionId));
    }
}
