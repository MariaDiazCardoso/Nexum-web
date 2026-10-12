package com.nexum.repository;

import com.nexum.entity.IaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IaHistorialRepository extends JpaRepository<IaHistorial, Integer> {
    List<IaHistorial> findByUsuario_Id(UUID usuarioId);
    List<IaHistorial> findByCurso_Id(UUID cursoId);
    List<IaHistorial> findByLeccion_Id(UUID leccionId);
    List<IaHistorial> findByUsuario_IdAndCurso_Id(UUID usuarioId, UUID cursoId);
    List<IaHistorial> findByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);
    List<IaHistorial> findByCurso_IdAndLeccion_Id(UUID cursoId, UUID leccionId);
    List<IaHistorial> findByUsuario_IdAndCurso_IdAndLeccion_Id(UUID usuarioId, UUID cursoId, UUID leccionId);
}
