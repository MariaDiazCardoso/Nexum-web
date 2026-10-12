package com.nexum.repository;

import com.nexum.entity.ProgresoLeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProgresoLeccionRepository extends JpaRepository<ProgresoLeccion, UUID> {

    List<ProgresoLeccion> findByUsuario_Id(UUID usuarioId);

    List<ProgresoLeccion> findByLeccion_Id(UUID leccionId);

    List<ProgresoLeccion> findByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);

    boolean existsByUsuario_IdAndLeccion_Id(UUID usuarioId, UUID leccionId);

    @Query("""
            SELECT p FROM ProgresoLeccion p
            JOIN p.leccion l
            JOIN l.modulo m
            WHERE (:usuarioId IS NULL OR p.usuario.id = :usuarioId)
              AND (:cursoId IS NULL OR m.curso.id = :cursoId)
              AND (:leccionId IS NULL OR l.id = :leccionId)
            """)
    List<ProgresoLeccion> buscarConFiltros(@Param("usuarioId") UUID usuarioId,
                                           @Param("cursoId") UUID cursoId,
                                           @Param("leccionId") UUID leccionId);

    @Query("""
            SELECT COUNT(p) FROM ProgresoLeccion p
            JOIN p.leccion l
            JOIN l.modulo m
            WHERE p.usuario.id = :usuarioId
              AND m.curso.id = :cursoId
              AND p.completado = true
            """)
    long contarLeccionesCompletadas(@Param("usuarioId") UUID usuarioId,
                                    @Param("cursoId") UUID cursoId);
}
