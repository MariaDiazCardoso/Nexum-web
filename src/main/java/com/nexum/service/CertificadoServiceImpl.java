package com.nexum.service;

import com.nexum.dto.CertificadoRequest;
import com.nexum.dto.CertificadoResponse;
import com.nexum.entity.Certificado;
import com.nexum.entity.Inscripcion;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.CertificadoRepository;
import com.nexum.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificadoServiceImpl implements CertificadoService {

    private static final BigDecimal PROGRESO_COMPLETO = new BigDecimal("100.00");

    private final CertificadoRepository certificadoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AccesoService accesoService;

    @Override
    public CertificadoResponse crear(CertificadoRequest request) {
        accesoService.exigirAdministrador();
        Inscripcion inscripcion = obtenerInscripcion(request.inscripcionId());
        validarProgresoCompleto(inscripcion);
        validarInscripcionSinCertificado(request.inscripcionId());

        Certificado certificado = Certificado.builder()
                .inscripcion(inscripcion)
                .codigoVerificacion(generarCodigoUnico())
                .urlPdf(request.urlPdf())
                .build();

        return CertificadoResponse.desde(certificadoRepository.save(certificado));
    }

    @Override
    public List<CertificadoResponse> listar(UUID usuarioId, String codigo) {
        if (codigo != null && !codigo.isBlank()) {
            Certificado certificado = certificadoRepository.findByCodigoVerificacion(codigo)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Certificado no encontrado con código: " + codigo));
            accesoService.verificar(certificado.getInscripcion().getUsuario().getId());
            return List.of(CertificadoResponse.desde(certificado));
        }

        List<Certificado> certificados;
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            certificados = certificadoRepository.findByInscripcion_Usuario_Id(usuarioId);
        } else {
            accesoService.exigirAdministrador();
            certificados = certificadoRepository.findAll();
        }

        return certificados.stream()
                .map(CertificadoResponse::desde)
                .toList();
    }

    @Override
    public CertificadoResponse obtenerPorId(UUID id) {
        Certificado certificado = buscarOLanzar(id);
        accesoService.verificar(certificado.getInscripcion().getUsuario().getId());
        return CertificadoResponse.desde(certificado);
    }

    @Override
    public CertificadoResponse actualizar(UUID id, CertificadoRequest request) {
        accesoService.exigirAdministrador();
        Certificado certificado = buscarOLanzar(id);
        Inscripcion inscripcion = obtenerInscripcion(request.inscripcionId());
        validarProgresoCompleto(inscripcion);
        validarInscripcionSinOtroCertificado(request.inscripcionId(), id);

        certificado.setInscripcion(inscripcion);
        certificado.setUrlPdf(request.urlPdf());
        return CertificadoResponse.desde(certificadoRepository.save(certificado));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        Certificado certificado = buscarOLanzar(id);
        certificadoRepository.delete(certificado);
    }

    private Certificado buscarOLanzar(UUID id) {
        return certificadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Certificado no encontrado con id: " + id));
    }

    private Inscripcion obtenerInscripcion(UUID inscripcionId) {
        return inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inscripción no encontrada con id: " + inscripcionId));
    }

    private void validarProgresoCompleto(Inscripcion inscripcion) {
        BigDecimal progreso = inscripcion.getProgresoPorcentaje();
        if (progreso == null || progreso.compareTo(PROGRESO_COMPLETO) != 0) {
            throw new SolicitudInvalidaException("Solo se puede emitir certificado con progreso del 100%");
        }
    }

    private void validarInscripcionSinCertificado(UUID inscripcionId) {
        if (certificadoRepository.existsByInscripcion_Id(inscripcionId)) {
            throw new ConflictoException("Ya existe un certificado para la inscripción indicada");
        }
    }

    private void validarInscripcionSinOtroCertificado(UUID inscripcionId, UUID id) {
        if (certificadoRepository.existsByInscripcion_IdAndIdNot(inscripcionId, id)) {
            throw new ConflictoException("Ya existe otro certificado para la inscripción indicada");
        }
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } while (certificadoRepository.existsByCodigoVerificacion(codigo));
        return codigo;
    }
}
