package com.nexum.service;

import com.nexum.dto.PagoRequest;
import com.nexum.dto.PagoResponse;
import com.nexum.entity.Pago;
import com.nexum.entity.Suscripcion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.PagoRepository;
import com.nexum.repository.SuscripcionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final AccesoService accesoService;

    @Override
    public PagoResponse crear(PagoRequest request) {
        accesoService.exigirAdministrador();
        Pago pago = new Pago();
        aplicar(pago, request);
        return PagoResponse.desde(pagoRepository.save(pago));
    }

    @Override
    public List<PagoResponse> listar(UUID usuarioId) {
        List<Pago> pagos;
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            pagos = pagoRepository.findByUsuario_Id(usuarioId);
        } else {
            accesoService.exigirAdministrador();
            pagos = pagoRepository.findAll();
        }
        return pagos.stream().map(PagoResponse::desde).toList();
    }

    @Override
    public PagoResponse obtenerPorId(UUID id) {
        Pago pago = buscarOLanzar(id);
        accesoService.verificar(pago.getUsuario().getId());
        return PagoResponse.desde(pago);
    }

    @Override
    public PagoResponse actualizar(UUID id, PagoRequest request) {
        accesoService.exigirAdministrador();
        Pago pago = buscarOLanzar(id);
        aplicar(pago, request);
        return PagoResponse.desde(pagoRepository.save(pago));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        pagoRepository.delete(buscarOLanzar(id));
    }

    private void aplicar(Pago pago, PagoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        Suscripcion suscripcion = null;
        if (request.suscripcionId() != null) {
            suscripcion = suscripcionRepository.findById(request.suscripcionId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada con id: " + request.suscripcionId()));
            if (!suscripcion.getUsuario().getId().equals(request.usuarioId())) {
                throw new SolicitudInvalidaException("La suscripción debe pertenecer al mismo usuario del pago");
            }
        }
        pago.setUsuario(usuario);
        pago.setSuscripcion(suscripcion);
        pago.setMonto(request.monto());
        pago.setMetodo(request.metodo());
        pago.setEstado(request.estado());
        pago.setConcepto(request.concepto());
        pago.setReferencia(request.referencia());
    }

    private Pago buscarOLanzar(UUID id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));
    }
}
