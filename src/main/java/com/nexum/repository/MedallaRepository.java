package com.nexum.repository;

import com.nexum.entity.Medalla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedallaRepository extends JpaRepository<Medalla, UUID> {
}
