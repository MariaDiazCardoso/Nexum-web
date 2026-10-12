package com.nexum.repository;

import com.nexum.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, UUID> {
    List<Suscripcion> findByUsuario_Id(UUID usuarioId);
}
