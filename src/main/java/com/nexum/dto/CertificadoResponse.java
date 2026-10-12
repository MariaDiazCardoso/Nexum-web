package com.nexum.dto;

import com.nexum.entity.Certificado;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificadoResponse(
        UUID id,
        UUID inscripcionId,
        UUID usuarioId,
        UUID cursoId,
        String codigoVerificacion,
        String urlPdf,
        LocalDateTime fechaEmision
) {
    public static CertificadoResponse desde(Certificado certificado) {
        return new CertificadoResponse(
                certificado.getId(),
                certificado.getInscripcion() != null ? certificado.getInscripcion().getId() : null,
                certificado.getInscripcion() != null && certificado.getInscripcion().getUsuario() != null
                        ? certificado.getInscripcion().getUsuario().getId()
                        : null,
                certificado.getInscripcion() != null && certificado.getInscripcion().getCurso() != null
                        ? certificado.getInscripcion().getCurso().getId()
                        : null,
                certificado.getCodigoVerificacion(),
                certificado.getUrlPdf(),
                certificado.getFechaEmision()
        );
    }
}
