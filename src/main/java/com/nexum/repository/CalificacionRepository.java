package com.nexum.repository;

import com.nexum.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalificacionRepository extends JpaRepository<Calificacion, UUID> {

    List<Calificacion> findByUsuario_Id(UUID usuarioId);

    List<Calificacion> findByCurso_Id(UUID cursoId);

    List<Calificacion> findByUsuario_IdAndCurso_Id(UUID usuarioId, UUID cursoId);
}
