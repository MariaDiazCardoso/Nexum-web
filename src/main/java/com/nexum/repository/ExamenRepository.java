package com.nexum.repository;

import com.nexum.entity.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamenRepository extends JpaRepository<Examen, UUID> {

    List<Examen> findByLeccion_Id(UUID leccionId);

    boolean existsByLeccion_Id(UUID leccionId);

    boolean existsByLeccion_IdAndIdNot(UUID leccionId, UUID id);
}
