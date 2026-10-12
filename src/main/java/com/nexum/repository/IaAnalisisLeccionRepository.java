package com.nexum.repository;

import com.nexum.entity.IaAnalisisLeccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IaAnalisisLeccionRepository extends JpaRepository<IaAnalisisLeccion, Integer> {
    List<IaAnalisisLeccion> findByUsuario_Id(UUID usuarioId);
    List<IaAnalisisLeccion> findByLeccion_Id(UUID leccionId);
    List<IaAnalisisLeccion> findByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);
}
