package com.nexum.repository;

import com.nexum.entity.SeguimientoLeccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeguimientoLeccionRepository extends JpaRepository<SeguimientoLeccion, UUID> {

    List<SeguimientoLeccion> findByUsuario_Id(UUID usuarioId);

    List<SeguimientoLeccion> findByLeccion_Id(UUID leccionId);

    List<SeguimientoLeccion> findByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);

    boolean existsByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);
}
