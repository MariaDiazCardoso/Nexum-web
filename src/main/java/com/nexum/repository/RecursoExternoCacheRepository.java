package com.nexum.repository;

import com.nexum.entity.RecursoExternoCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecursoExternoCacheRepository extends JpaRepository<RecursoExternoCache, UUID> {
    List<RecursoExternoCache> findByLeccion_Id(UUID leccionId);
}
