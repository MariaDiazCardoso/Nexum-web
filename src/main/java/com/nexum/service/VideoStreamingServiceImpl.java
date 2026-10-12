package com.nexum.service;

import com.nexum.dto.VideoStreamingRequest;
import com.nexum.dto.VideoStreamingResponse;
import com.nexum.entity.Leccion;
import com.nexum.entity.VideoStreaming;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.VideoStreamingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoStreamingServiceImpl implements VideoStreamingService {

    private final VideoStreamingRepository videoStreamingRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public VideoStreamingResponse crear(VideoStreamingRequest request) {
        accesoService.exigirAdministrador();
        if (videoStreamingRepository.existsByLeccion_Id(request.leccionId())) {
            throw new ConflictoException("Ya existe un video streaming para la lección indicada");
        }
        VideoStreaming video = new VideoStreaming();
        aplicar(video, request);
        return VideoStreamingResponse.desde(videoStreamingRepository.save(video));
    }

    @Override
    public List<VideoStreamingResponse> listar(UUID leccionId) {
        List<VideoStreaming> videos = leccionId == null
                ? videoStreamingRepository.findAll()
                : videoStreamingRepository.findByLeccion_Id(leccionId);
        return videos.stream().map(VideoStreamingResponse::desde).toList();
    }

    @Override
    public VideoStreamingResponse obtenerPorId(UUID id) {
        return VideoStreamingResponse.desde(buscarOLanzar(id));
    }

    @Override
    public VideoStreamingResponse actualizar(UUID id, VideoStreamingRequest request) {
        accesoService.exigirAdministrador();
        if (videoStreamingRepository.existsByLeccion_IdAndIdNot(request.leccionId(), id)) {
            throw new ConflictoException("Ya existe otro video streaming para la lección indicada");
        }
        VideoStreaming video = buscarOLanzar(id);
        aplicar(video, request);
        return VideoStreamingResponse.desde(videoStreamingRepository.save(video));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        videoStreamingRepository.delete(buscarOLanzar(id));
    }

    private void aplicar(VideoStreaming video, VideoStreamingRequest request) {
        video.setLeccion(obtenerLeccion(request.leccionId()));
        video.setAwsS3Key(request.awsS3Key());
        video.setAwsCloudfrontUrl(request.awsCloudfrontUrl());
        video.setDuracionSegundos(request.duracionSegundos());
        video.setCalidadDisponible(request.calidadDisponible());
    }

    private VideoStreaming buscarOLanzar(UUID id) {
        return videoStreamingRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Video streaming no encontrado con id: " + id));
    }

    private Leccion obtenerLeccion(UUID id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + id));
    }
}
