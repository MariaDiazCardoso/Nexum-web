package com.nexum.repository;

import com.nexum.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ModuloRepository extends JpaRepository<Modulo, UUID> {

    List<Modulo> findAllByOrderByOrdenAsc();

    List<Modulo> findByCurso_IdOrderByOrdenAsc(UUID cursoId);

    boolean existsByCurso_Id(UUID cursoId);

    boolean existsByCurso_IdAndOrden(UUID cursoId, Integer orden);

    boolean existsByCurso_IdAndOrdenAndIdNot(UUID cursoId, Integer orden, UUID id);

    @Query(value = "SELECT COUNT(*) FROM leccion WHERE modulo_id = ?1", nativeQuery = true)
    long contarLeccionesPorModulo(UUID moduloId);
}
