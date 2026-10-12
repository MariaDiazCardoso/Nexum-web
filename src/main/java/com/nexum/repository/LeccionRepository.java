package com.nexum.repository;

import com.nexum.entity.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LeccionRepository extends JpaRepository<Leccion, UUID> {

    List<Leccion> findAllByOrderByOrdenAsc();

    List<Leccion> findByModulo_IdOrderByOrdenAsc(UUID moduloId);

    @Query("SELECT l FROM Leccion l JOIN l.modulo m WHERE m.curso.id = :cursoId ORDER BY m.orden, l.orden")
    List<Leccion> buscarPorCurso(@Param("cursoId") UUID cursoId);

    boolean existsByModulo_Id(UUID moduloId);

    boolean existsByModulo_IdAndOrden(UUID moduloId, Integer orden);

    boolean existsByModulo_IdAndOrdenAndIdNot(UUID moduloId, Integer orden, UUID id);

    @Query("SELECT COUNT(l) FROM Leccion l JOIN l.modulo m WHERE m.curso.id = :cursoId")
    long contarPorCurso(@Param("cursoId") UUID cursoId);
}
