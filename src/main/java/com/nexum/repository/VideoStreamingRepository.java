package com.nexum.repository;

import com.nexum.entity.VideoStreaming;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoStreamingRepository extends JpaRepository<VideoStreaming, UUID> {
    List<VideoStreaming> findByLeccion_Id(UUID leccionId);
    boolean existsByLeccion_Id(UUID leccionId);
    boolean existsByLeccion_IdAndIdNot(UUID leccionId, UUID id);
}
