package com.nexum.service;

import com.nexum.dto.MedallaRequest;
import com.nexum.dto.MedallaResponse;
import com.nexum.entity.Medalla;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.MedallaRepository;
import com.nexum.repository.UsuarioMedallaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedallaServiceImpl implements MedallaService {

    private final MedallaRepository medallaRepository;
    private final UsuarioMedallaRepository usuarioMedallaRepository;
    private final AccesoService accesoService;

    @Override
    public MedallaResponse crear(MedallaRequest request) {
        accesoService.exigirAdministrador();
        Medalla medalla = new Medalla();
        aplicar(medalla, request);
        return MedallaResponse.desde(medallaRepository.save(medalla));
    }

    @Override
    public List<MedallaResponse> listar() {
        return medallaRepository.findAll().stream().map(MedallaResponse::desde).toList();
    }

    @Override
    public MedallaResponse obtenerPorId(UUID id) {
        return MedallaResponse.desde(buscarOLanzar(id));
    }

    @Override
    public MedallaResponse actualizar(UUID id, MedallaRequest request) {
        accesoService.exigirAdministrador();
        Medalla medalla = buscarOLanzar(id);
        aplicar(medalla, request);
        return MedallaResponse.desde(medallaRepository.save(medalla));
    }

    @Override
    public void eliminar(UUID id) {
        accesoService.exigirAdministrador();
        Medalla medalla = buscarOLanzar(id);
        if (usuarioMedallaRepository.existsByMedalla_Id(id)) {
            throw new ConflictoException("No se puede eliminar la medalla porque está asignada");
        }
        medallaRepository.delete(medalla);
    }

    private void aplicar(Medalla medalla, MedallaRequest request) {
        medalla.setNombre(request.nombre());
        medalla.setDescripcion(request.descripcion());
        medalla.setImagenUrl(request.imagenUrl());
    }

    private Medalla buscarOLanzar(UUID id) {
        return medallaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medalla no encontrada con id: " + id));
    }
}
