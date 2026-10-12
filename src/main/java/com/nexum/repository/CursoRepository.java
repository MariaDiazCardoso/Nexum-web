package com.nexum.repository;

import com.nexum.entity.Curso;
import com.nexum.entity.EstadoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/** No lleva @Repository: Spring Data la detecta automáticamente. */
public interface CursoRepository extends JpaRepository<Curso, UUID> {

    // ── Unicidad de slug ───────────────────────────────────────────────────────
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    // ── Verificar cursos de una categoría (usado por CategoriaServiceImpl.eliminar) ───
    boolean existsByCategorias_Id(UUID categoriaId);

    // ── Filtros de lista (T05) ────────────────────────────────────────────────
    List<Curso> findByEstado(EstadoCurso estado);

    List<Curso> findByCategorias_Id(UUID categoriaId);

    List<Curso> findByCategorias_IdAndEstado(UUID categoriaId, EstadoCurso estado);

    // ── JPQL obligatoria T10b ──────────────────────────────────────────────────
    @Query("SELECT c FROM Curso c JOIN c.categorias cat " +
           "WHERE cat.nombre = :nombre AND c.estado = :estado")
    List<Curso> buscarPorCategoria(@Param("nombre") String nombre,
                                   @Param("estado") EstadoCurso estado);
}

