package com.nexum.repository;

import com.nexum.entity.CodigoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, UUID> {
    List<CodigoVerificacion> findByUsuario_Id(UUID usuarioId);
}
