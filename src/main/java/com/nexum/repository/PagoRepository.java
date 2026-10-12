package com.nexum.repository;

import com.nexum.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    List<Pago> findByUsuario_Id(UUID usuarioId);
}
