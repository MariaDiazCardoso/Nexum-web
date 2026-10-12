package com.nexum.service;

import com.nexum.dto.ComentarioLeccionRequest;
import com.nexum.dto.ComentarioLeccionResponse;
import com.nexum.entity.ComentarioLeccion;
import com.nexum.entity.Leccion;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.ComentarioLeccionRepository;
import com.nexum.repository.LeccionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioLeccionServiceImpl implements ComentarioLeccionService {

    private final ComentarioLeccionRepository comentarioLeccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;
    private final AccesoService accesoService;

    @Override
    public ComentarioLeccionResponse crear(ComentarioLeccionRequest request) {
        accesoService.verificar(request.usuarioId());
        ComentarioLeccion comentario = new ComentarioLeccion();
        aplicar(comentario, request);
        return ComentarioLeccionResponse.desde(comentarioLeccionRepository.save(comentario));
    }

    @Override
    public List<ComentarioLeccionResponse> listar(UUID leccionId) {
        List<ComentarioLeccion> comentarios = leccionId == null
                ? comentarioLeccionRepository.findAll()
                : comentarioLeccionRepository.findByLeccion_Id(leccionId);
        return comentarios.stream().map(ComentarioLeccionResponse::desde).toList();
    }

    @Override
    public ComentarioLeccionResponse obtenerPorId(UUID id) {
        return ComentarioLeccionResponse.desde(buscarOLanzar(id));
    }

    @Override
    public ComentarioLeccionResponse actualizar(UUID id, ComentarioLeccionRequest request) {
        ComentarioLeccion comentario = buscarOLanzar(id);
        accesoService.verificar(comentario.getUsuario().getId());
        aplicar(comentario, request);
        return ComentarioLeccionResponse.desde(comentarioLeccionRepository.save(comentario));
    }

    @Override
    public void eliminar(UUID id) {
        ComentarioLeccion comentario = buscarOLanzar(id);
        accesoService.verificar(comentario.getUsuario().getId());
        if (comentarioLeccionRepository.existsByComentarioPadre_Id(id)) {
            throw new ConflictoException("No se puede eliminar el comentario porque tiene respuestas asociadas");
        }
        comentarioLeccionRepository.delete(comentario);
    }

    private void aplicar(ComentarioLeccion comentario, ComentarioLeccionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        Leccion leccion = leccionRepository.findById(request.leccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada con id: " + request.leccionId()));
        ComentarioLeccion padre = null;
        if (request.comentarioPadreId() != null) {
            padre = buscarOLanzar(request.comentarioPadreId());
            if (!padre.getLeccion().getId().equals(request.leccionId())) {
                throw new ConflictoException("El comentario padre debe pertenecer a la misma lección");
            }
        }
        comentario.setUsuario(usuario);
        comentario.setLeccion(leccion);
        comentario.setComentarioPadre(padre);
        comentario.setContenido(request.contenido());
    }

    private ComentarioLeccion buscarOLanzar(UUID id) {
        return comentarioLeccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con id: " + id));
    }
}
