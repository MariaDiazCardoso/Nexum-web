package com.nexum.service;

import com.nexum.dto.ProgresoLeccionPatchRequest;
import com.nexum.dto.ProgresoLeccionRequest;
import com.nexum.dto.ProgresoLeccionResponse;
import com.nexum.entity.Curso;
import com.nexum.entity.Inscripcion;
import com.nexum.entity.Leccion;
import com.nexum.entity.ProgresoLeccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.InscripcionRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.ProgresoLeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgresoLeccionServiceImpl implements ProgresoLeccionService {

    private final ProgresoLeccionRepository progresoLeccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AccesoService accesoService;

    @Override
    @Transactional
    public ProgresoLeccionResponse crear(ProgresoLeccionRequest request) {
        accesoService.verificar(request.usuarioId());
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Leccion leccion = obtenerLeccion(request.leccionId());
        Curso curso = leccion.getModulo().getCurso();

        if (progresoLeccionRepository.existsByUsuario_IdAndLeccion_Id(request.usuarioId(), request.leccionId())) {
            throw new ConflictoException("Ya existe un progreso para el usuario y lección indicados");
        }

        obtenerInscripcionActiva(request.usuarioId(), curso.getId());

        boolean completado = Boolean.TRUE.equals(request.completado());
        ProgresoLeccion progreso = ProgresoLeccion.builder()
                .usuario(usuario)
                .leccion(leccion)
                .completado(completado)
                .ultimoSegundoVisto(request.ultimoSegundoVisto())
                .fechaCompletado(completado ? LocalDateTime.now() : null)
                .tiempoActivo(request.tiempoActivo())
                .build();

        ProgresoLeccion guardado = progresoLeccionRepository.save(progreso);
        if (completado) {
            sumarXpPrimeraVez(guardado);
        }
        recalcularProgreso(request.usuarioId(), curso.getId());

        return ProgresoLeccionResponse.desde(guardado);
    }

    @Override
    public List<ProgresoLeccionResponse> listar(UUID usuarioId, UUID cursoId, UUID leccionId) {
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
        } else {
            accesoService.exigirAdministrador();
        }
        return progresoLeccionRepository.buscarConFiltros(usuarioId, cursoId, leccionId)
                .stream()
                .map(ProgresoLeccionResponse::desde)
                .toList();
    }

    @Override
    public ProgresoLeccionResponse obtenerPorId(UUID id) {
        ProgresoLeccion progreso = buscarOLanzar(id);
        accesoService.verificar(progreso.getUsuario().getId());
        return ProgresoLeccionResponse.desde(progreso);
    }

    @Override
    @Transactional
    public ProgresoLeccionResponse actualizarParcial(UUID id, ProgresoLeccionPatchRequest request) {
        ProgresoLeccion progreso = buscarOLanzar(id);
        accesoService.verificar(progreso.getUsuario().getId());
        Curso curso = progreso.getLeccion().getModulo().getCurso();
        obtenerInscripcionActiva(progreso.getUsuario().getId(), curso.getId());

        boolean estabaCompletado = Boolean.TRUE.equals(progreso.getCompletado());

        if (request.completado() != null) {
            progreso.setCompletado(request.completado());
            if (Boolean.TRUE.equals(request.completado())) {
                if (!estabaCompletado) {
                    progreso.setFechaCompletado(LocalDateTime.now());
                    sumarXpPrimeraVez(progreso);
                }
            } else {
                progreso.setFechaCompletado(null);
            }
        }
        if (request.ultimoSegundoVisto() != null) {
            progreso.setUltimoSegundoVisto(request.ultimoSegundoVisto());
        }
        if (request.tiempoActivo() != null) {
            progreso.setTiempoActivo(request.tiempoActivo());
        }

        ProgresoLeccion guardado = progresoLeccionRepository.save(progreso);
        if (request.completado() != null && estabaCompletado != Boolean.TRUE.equals(request.completado())) {
            recalcularProgreso(progreso.getUsuario().getId(), curso.getId());
        }

        return ProgresoLeccionResponse.desde(guardado);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        ProgresoLeccion progreso = buscarOLanzar(id);
        accesoService.verificar(progreso.getUsuario().getId());
        UUID usuarioId = progreso.getUsuario().getId();
        UUID cursoId = progreso.getLeccion().getModulo().getCurso().getId();
        progresoLeccionRepository.delete(progreso);
        recalcularProgreso(usuarioId, cursoId);
    }

    private ProgresoLeccion buscarOLanzar(UUID id) {
        return progresoLeccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Progreso de lección no encontrado con id: " + id));
    }

    private Usuario obtenerUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + usuarioId));
    }

    private Leccion obtenerLeccion(UUID leccionId) {
        return leccionRepository.findById(leccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lección no encontrada con id: " + leccionId));
    }

    private Inscripcion obtenerInscripcionActiva(UUID usuarioId, UUID cursoId) {
        return inscripcionRepository.findByUsuario_IdAndCurso_IdAndEstado(
                        usuarioId,
                        cursoId,
                        Inscripcion.ESTADO_ACTIVA)
                .orElseThrow(() -> new SolicitudInvalidaException(
                        "El usuario debe tener una inscripción activa en el curso de la lección"));
    }

    private void recalcularProgreso(UUID usuarioId, UUID cursoId) {
        Inscripcion inscripcion = obtenerInscripcionActiva(usuarioId, cursoId);
        long totalLecciones = leccionRepository.contarPorCurso(cursoId);
        BigDecimal porcentaje = BigDecimal.ZERO;

        if (totalLecciones > 0) {
            long completadas = progresoLeccionRepository.contarLeccionesCompletadas(usuarioId, cursoId);
            porcentaje = BigDecimal.valueOf(completadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalLecciones), 2, RoundingMode.HALF_UP);
        }

        inscripcion.setProgresoPorcentaje(porcentaje);
        inscripcionRepository.save(inscripcion);
    }

    private void sumarXpPrimeraVez(ProgresoLeccion progreso) {
        if (Boolean.TRUE.equals(progreso.getXpOtorgadaAplicada())) {
            return;
        }
        Usuario usuario = progreso.getUsuario();
        Leccion leccion = progreso.getLeccion();
        int xp = leccion.getXpOtorgada() == null ? 0 : leccion.getXpOtorgada();
        if (xp == 0) {
            progreso.setXpOtorgadaAplicada(true);
            return;
        }
        int puntosActuales = usuario.getPuntosXp() == null ? 0 : usuario.getPuntosXp();
        usuario.setPuntosXp(puntosActuales + xp);
        progreso.setXpOtorgadaAplicada(true);
        usuarioRepository.save(usuario);
    }
}
