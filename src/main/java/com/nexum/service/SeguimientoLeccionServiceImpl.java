package com.nexum.service;

import com.nexum.dto.SeguimientoLeccionPatchRequest;
import com.nexum.dto.SeguimientoLeccionRequest;
import com.nexum.dto.SeguimientoLeccionResponse;
import com.nexum.entity.Leccion;
import com.nexum.entity.SeguimientoLeccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.SeguimientoLeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeguimientoLeccionServiceImpl implements SeguimientoLeccionService {

    private final SeguimientoLeccionRepository seguimientoLeccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public SeguimientoLeccionResponse crear(SeguimientoLeccionRequest request) {
        accesoService.verificar(request.usuarioId());
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Leccion leccion = obtenerLeccion(request.leccionId());

        if (seguimientoLeccionRepository.existsByUsuario_IdAndLeccion_Id(
                request.usuarioId(), request.leccionId())) {
            throw new ConflictoException("Ya existe un seguimiento para el usuario y lección indicados");
        }

        SeguimientoLeccion seguimiento = SeguimientoLeccion.builder()
                .usuario(usuario)
                .leccion(leccion)
                .tiempoAcumulado(request.tiempoAcumulado() != null ? request.tiempoAcumulado() : 0)
                .cantidadVisitas(request.cantidadVisitas() != null ? request.cantidadVisitas() : 0)
                .intentosPractica(request.intentosPractica() != null ? request.intentosPractica() : 0)
                .build();

        return SeguimientoLeccionResponse.desde(seguimientoLeccionRepository.save(seguimiento));
    }

    @Override
    public List<SeguimientoLeccionResponse> listar(UUID usuarioId, UUID leccionId) {
        List<SeguimientoLeccion> seguimientos;

        if (usuarioId != null && leccionId != null) {
            accesoService.verificar(usuarioId);
            seguimientos = seguimientoLeccionRepository.findByUsuario_IdAndLeccion_Id(usuarioId, leccionId);
        } else if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            seguimientos = seguimientoLeccionRepository.findByUsuario_Id(usuarioId);
        } else if (leccionId != null) {
            accesoService.exigirAdministrador();
            seguimientos = seguimientoLeccionRepository.findByLeccion_Id(leccionId);
        } else {
            accesoService.exigirAdministrador();
            seguimientos = seguimientoLeccionRepository.findAll();
        }

        return seguimientos.stream()
                .map(SeguimientoLeccionResponse::desde)
                .toList();
    }

    @Override
    public SeguimientoLeccionResponse obtenerPorId(UUID id) {
        SeguimientoLeccion seguimiento = buscarOLanzar(id);
        accesoService.verificar(seguimiento.getUsuario().getId());
        return SeguimientoLeccionResponse.desde(seguimiento);
    }

    @Override
    public SeguimientoLeccionResponse actualizarParcial(UUID id, SeguimientoLeccionPatchRequest request) {
        SeguimientoLeccion seguimiento = buscarOLanzar(id);
        accesoService.verificar(seguimiento.getUsuario().getId());

        if (request.tiempoAcumulado() != null) {
            seguimiento.setTiempoAcumulado(request.tiempoAcumulado());
        }
        if (request.cantidadVisitas() != null) {
            seguimiento.setCantidadVisitas(request.cantidadVisitas());
        }
        if (request.intentosPractica() != null) {
            seguimiento.setIntentosPractica(request.intentosPractica());
        }
        seguimiento.setUltimaActividad(LocalDateTime.now());

        return SeguimientoLeccionResponse.desde(seguimientoLeccionRepository.save(seguimiento));
    }

    private SeguimientoLeccion buscarOLanzar(UUID id) {
        return seguimientoLeccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Seguimiento de lección no encontrado con id: " + id));
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
}
