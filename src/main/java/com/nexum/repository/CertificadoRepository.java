package com.nexum.repository;

import com.nexum.entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificadoRepository extends JpaRepository<Certificado, UUID> {

    List<Certificado> findByInscripcion_Usuario_Id(UUID usuarioId);

    Optional<Certificado> findByCodigoVerificacion(String codigoVerificacion);

    boolean existsByInscripcion_Id(UUID inscripcionId);

    boolean existsByInscripcion_IdAndIdNot(UUID inscripcionId, UUID id);

    boolean existsByCodigoVerificacion(String codigoVerificacion);
}
