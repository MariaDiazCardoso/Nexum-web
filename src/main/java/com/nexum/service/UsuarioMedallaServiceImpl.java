package com.nexum.service;

import com.nexum.dto.UsuarioMedallaRequest;
import com.nexum.dto.UsuarioMedallaResponse;
import com.nexum.entity.Medalla;
import com.nexum.entity.Usuario;
import com.nexum.entity.UsuarioMedalla;
import com.nexum.entity.UsuarioMedallaId;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.MedallaRepository;
import com.nexum.repository.UsuarioMedallaRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioMedallaServiceImpl implements UsuarioMedallaService {

    private final UsuarioMedallaRepository usuarioMedallaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedallaRepository medallaRepository;
    private final AccesoService accesoService;

    @Override
    public UsuarioMedallaResponse asignar(UUID usuarioId, UsuarioMedallaRequest request) {
        accesoService.exigirAdministrador();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + usuarioId));
        Medalla medalla = medallaRepository.findById(request.medallaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medalla no encontrada con id: " + request.medallaId()));
        UsuarioMedallaId id = new UsuarioMedallaId(usuarioId, request.medallaId());

        if (usuarioMedallaRepository.existsById(id)) {
            throw new ConflictoException("La medalla ya está asignada al usuario");
        }

        UsuarioMedalla usuarioMedalla = UsuarioMedalla.builder()
                .id(id)
                .usuario(usuario)
                .medalla(medalla)
                .build();

        return UsuarioMedallaResponse.desde(usuarioMedallaRepository.save(usuarioMedalla));
    }

    @Override
    public List<UsuarioMedallaResponse> listar(UUID usuarioId) {
        accesoService.verificar(usuarioId);
        return usuarioMedallaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(UsuarioMedallaResponse::desde)
                .toList();
    }

    @Override
    public void eliminar(UUID usuarioId, UUID medallaId) {
        accesoService.exigirAdministrador();
        UsuarioMedallaId id = new UsuarioMedallaId(usuarioId, medallaId);
        UsuarioMedalla usuarioMedalla = usuarioMedallaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medalla de usuario no encontrada"));
        usuarioMedallaRepository.delete(usuarioMedalla);
    }
}
