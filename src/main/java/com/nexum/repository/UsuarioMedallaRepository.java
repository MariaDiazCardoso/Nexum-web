package com.nexum.repository;

import com.nexum.entity.UsuarioMedalla;
import com.nexum.entity.UsuarioMedallaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsuarioMedallaRepository extends JpaRepository<UsuarioMedalla, UsuarioMedallaId> {
    List<UsuarioMedalla> findByUsuario_Id(UUID usuarioId);
    boolean existsByMedalla_Id(UUID medallaId);
}
