package com.nexum.service;

import com.nexum.dto.SuscripcionRequest;
import com.nexum.dto.SuscripcionResponse;
import com.nexum.entity.Suscripcion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.SuscripcionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccesoService accesoService;

    @Override
    public SuscripcionResponse crear(SuscripcionRequest request) {
        accesoService.exigirAdministrador();
        validar(request);
        Suscripcion suscripcion = new Suscripcion();
        aplicar(suscripcion, request);
        return SuscripcionResponse.desde(suscripcionRepository.save(suscripcion));
    }

    @Override
    public List<SuscripcionResponse> listar(UUID usuarioId) {
        List<Suscripcion> suscripciones;
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            suscripciones = suscripcionRepository.findByUsuario_Id(usuarioId);
        } else {
            accesoService.exigirAdministrador();
            suscripciones = suscripcionRepository.findAll();
        }
        return suscripciones.stream().map(SuscripcionResponse::desde).toList();
    }

    @Override
    public SuscripcionResponse obtenerPorId(UUID id) {
        Suscripcion suscripcion = buscarOLanzar(id);
        accesoService.verificar(suscripcion.getUsuario().getId());
        return SuscripcionResponse.desde(suscripcion);
    }

    @Override
    public SuscripcionResponse actualizar(UUID id, SuscripcionRequest request) {
        accesoService.exigirAdministrador();
        validar(request);
        Suscripcion suscripcion = buscarOLanzar(id);
        aplicar(suscripcion, request);
        return SuscripcionResponse.desde(suscripcionRepository.save(suscripcion));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        suscripcionRepository.delete(buscarOLanzar(id));
    }

    private void aplicar(Suscripcion suscripcion, SuscripcionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        suscripcion.setUsuario(usuario);
        suscripcion.setPlan(request.plan());
        suscripcion.setPrecio(request.precio());
        suscripcion.setCreditosIa(request.creditosIa());
        suscripcion.setCreditosRestantes(request.creditosRestantes());
        suscripcion.setFechaInicio(request.fechaInicio());
        suscripcion.setFechaFin(request.fechaFin());
        suscripcion.setRenovacionAutomatica(request.renovacionAutomatica() != null && request.renovacionAutomatica());
        suscripcion.setEstado(request.estado());
    }

    private void validar(SuscripcionRequest request) {
        if (request.creditosRestantes() != null) {
            int creditosIa = request.creditosIa() == null ? 0 : request.creditosIa();
            if (request.creditosRestantes() < 0 || request.creditosRestantes() > creditosIa) {
                throw new SolicitudInvalidaException("Los créditos restantes deben estar entre 0 y creditosIa");
            }
        }
        if (request.fechaFin() != null && !request.fechaFin().isAfter(request.fechaInicio())) {
            throw new SolicitudInvalidaException("La fecha fin debe ser posterior a la fecha inicio");
        }
    }

    private Suscripcion buscarOLanzar(UUID id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada con id: " + id));
    }
}
