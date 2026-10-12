package com.nexum.repository;

import com.nexum.entity.ComentarioLeccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioLeccionRepository extends JpaRepository<ComentarioLeccion, UUID> {
    List<ComentarioLeccion> findByLeccion_Id(UUID leccionId);
    boolean existsByComentarioPadre_Id(UUID comentarioPadreId);
}
