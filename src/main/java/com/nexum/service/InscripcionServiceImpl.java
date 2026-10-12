package com.nexum.service;

import com.nexum.dto.InscripcionPatchRequest;
import com.nexum.dto.InscripcionRequest;
import com.nexum.dto.InscripcionResponse;
import com.nexum.entity.Curso;
import com.nexum.entity.EstadoCurso;
import com.nexum.entity.Inscripcion;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.InscripcionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    @Override
    public InscripcionResponse crear(InscripcionRequest request) {
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Curso curso = obtenerCurso(request.cursoId());

        if (inscripcionRepository.existsByUsuario_IdAndCurso_Id(request.usuarioId(), request.cursoId())) {
            throw new ConflictoException("Ya existe una inscripción para el usuario y curso indicados");
        }
        if (curso.getEstado() != EstadoCurso.PUBLICADO) {
            throw new SolicitudInvalidaException("Solo se permiten inscripciones a cursos PUBLICADO");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .usuario(usuario)
                .curso(curso)
                .estado(request.estado() == null || request.estado().isBlank()
                        ? Inscripcion.ESTADO_ACTIVA
                        : request.estado())
                .progresoPorcentaje(BigDecimal.ZERO)
                .build();

        return InscripcionResponse.desde(inscripcionRepository.save(inscripcion));
    }

    @Override
    public List<InscripcionResponse> listar(UUID usuarioId, UUID cursoId) {
        List<Inscripcion> inscripciones;

        if (usuarioId != null && cursoId != null) {
            inscripciones = inscripcionRepository.findByUsuario_IdAndCurso_Id(usuarioId, cursoId);
        } else if (usuarioId != null) {
            inscripciones = inscripcionRepository.findByUsuario_Id(usuarioId);
        } else if (cursoId != null) {
            inscripciones = inscripcionRepository.findByCurso_Id(cursoId);
        } else {
            inscripciones = inscripcionRepository.findAll();
        }

        return inscripciones.stream()
                .map(InscripcionResponse::desde)
                .toList();
    }

    @Override
    public InscripcionResponse obtenerPorId(UUID id) {
        return InscripcionResponse.desde(buscarOLanzar(id));
    }

    @Override
    public InscripcionResponse actualizarEstado(UUID id, InscripcionPatchRequest request) {
        Inscripcion inscripcion = buscarOLanzar(id);
        if (request.estado() != null) {
            inscripcion.setEstado(request.estado());
        }
        return InscripcionResponse.desde(inscripcionRepository.save(inscripcion));
    }

    @Override
    public void eliminar(UUID id) {
        Inscripcion inscripcion = buscarOLanzar(id);
        inscripcionRepository.delete(inscripcion);
    }

    private Inscripcion buscarOLanzar(UUID id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inscripción no encontrada con id: " + id));
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
}
