package com.nexum.repository;

import com.nexum.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActividadRepository extends JpaRepository<Actividad, UUID> {

    List<Actividad> findByLeccion_Id(UUID leccionId);

    boolean existsByLeccion_Id(UUID leccionId);
}
