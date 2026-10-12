package com.nexum.repository;

import com.nexum.entity.RecursoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecursoAdjuntoRepository extends JpaRepository<RecursoAdjunto, UUID> {
    List<RecursoAdjunto> findByLeccion_Id(UUID leccionId);
}
