package com.nexum.repository;

import com.nexum.entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PreguntaRepository extends JpaRepository<Pregunta, UUID> {

    List<Pregunta> findByExamen_Id(UUID examenId);

    boolean existsByExamen_Id(UUID examenId);
}
