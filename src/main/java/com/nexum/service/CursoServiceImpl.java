package com.nexum.service;

import com.nexum.dto.CursoPatchRequest;
import com.nexum.dto.CursoRequest;
import com.nexum.dto.CursoResponse;
import com.nexum.entity.Categoria;
import com.nexum.entity.Curso;
import com.nexum.entity.EstadoCurso;
import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.CategoriaRepository;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.InscripcionRepository;
import com.nexum.repository.ModuloRepository;
import com.nexum.repository.UsuarioRepository;
import com.nexum.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ModuloRepository moduloRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    public CursoResponse crear(CursoRequest request) {
        Usuario responsable = obtenerResponsableAdministrador(request.responsableId());
        String slug = resolverSlug(request.slug(), request.titulo());
        validarSlugDisponible(slug);
        List<Categoria> categorias = obtenerCategorias(request.categoriaIds());

        Curso curso = Curso.builder()
                .titulo(request.titulo())
                .slug(slug)
                .descripcion(request.descripcion())
                .responsable(responsable)
                .precio(request.precio())
                .nivel(request.nivel())
                .estado(request.estado() != null ? request.estado() : EstadoCurso.BORRADOR)
                .recomendadoPara(request.recomendadoPara())
                .duracionEstimada(request.duracionEstimada())
                .ruta(request.ruta())
                .categoriaAcademica(request.categoriaAcademica())
                .categorias(categorias)
                .build();

        return CursoResponse.desde(cursoRepository.save(curso));
    }

    @Override
    public List<CursoResponse> listar(UUID categoriaId, EstadoCurso estado) {
        List<Curso> cursos;

        if (categoriaId != null && estado != null) {
            cursos = cursoRepository.findByCategorias_IdAndEstado(categoriaId, estado);
        } else if (categoriaId != null) {
            cursos = cursoRepository.findByCategorias_Id(categoriaId);
        } else if (estado != null) {
            cursos = cursoRepository.findByEstado(estado);
        } else {
            cursos = cursoRepository.findAll();
        }

        return cursos.stream()
                .map(CursoResponse::desde)
                .toList();
    }

    @Override
    public CursoResponse obtenerPorId(UUID id) {
        return CursoResponse.desde(buscarOLanzar(id));
    }

    @Override
    public CursoResponse actualizar(UUID id, CursoRequest request) {
        Curso curso = buscarOLanzar(id);
        Usuario responsable = obtenerResponsableAdministrador(request.responsableId());
        String slug = resolverSlug(request.slug(), request.titulo());
        validarSlugDisponibleParaOtroCurso(slug, id);
        List<Categoria> categorias = obtenerCategorias(request.categoriaIds());

        curso.setTitulo(request.titulo());
        curso.setSlug(slug);
        curso.setDescripcion(request.descripcion());
        curso.setResponsable(responsable);
        curso.setPrecio(request.precio());
        curso.setNivel(request.nivel());
        curso.setEstado(request.estado() != null ? request.estado() : EstadoCurso.BORRADOR);
        curso.setRecomendadoPara(request.recomendadoPara());
        curso.setDuracionEstimada(request.duracionEstimada());
        curso.setRuta(request.ruta());
        curso.setCategoriaAcademica(request.categoriaAcademica());
        curso.setCategorias(categorias);

        return CursoResponse.desde(cursoRepository.save(curso));
    }

    @Override
    public CursoResponse actualizarParcial(UUID id, CursoPatchRequest request) {
        Curso curso = buscarOLanzar(id);

        if (request.titulo() != null) {
            curso.setTitulo(request.titulo());
        }
        if (request.slug() != null) {
            String slug = resolverSlug(request.slug(), curso.getTitulo());
            validarSlugDisponibleParaOtroCurso(slug, id);
            curso.setSlug(slug);
        }
        if (request.descripcion() != null) {
            curso.setDescripcion(request.descripcion());
        }
        if (request.responsableId() != null) {
            curso.setResponsable(obtenerResponsableAdministrador(request.responsableId()));
        }
        if (request.precio() != null) {
            curso.setPrecio(request.precio());
        }
        if (request.nivel() != null) {
            curso.setNivel(request.nivel());
        }
        if (request.estado() != null) {
            curso.setEstado(request.estado());
        }
        if (request.recomendadoPara() != null) {
            curso.setRecomendadoPara(request.recomendadoPara());
        }
        if (request.duracionEstimada() != null) {
            curso.setDuracionEstimada(request.duracionEstimada());
        }
        if (request.ruta() != null) {
            curso.setRuta(request.ruta());
        }
        if (request.categoriaAcademica() != null) {
            curso.setCategoriaAcademica(request.categoriaAcademica());
        }
        if (request.categoriaIds() != null) {
            curso.setCategorias(obtenerCategorias(request.categoriaIds()));
        }

        return CursoResponse.desde(cursoRepository.save(curso));
    }

    @Override
    public void eliminar(UUID id) {
        Curso curso = buscarOLanzar(id);
        if (moduloRepository.existsByCurso_Id(id)) {
            throw new ConflictoException("No se puede eliminar el curso porque tiene módulos asociados");
        }
        if (inscripcionRepository.existsByCurso_Id(id)) {
            throw new ConflictoException("No se puede eliminar el curso porque tiene inscripciones asociadas");
        }
        cursoRepository.delete(curso);
    }

    private Curso buscarOLanzar(UUID id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con id: " + id));
    }

    private Usuario obtenerResponsableAdministrador(UUID responsableId) {
        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Responsable no encontrado con id: " + responsableId));

        if (responsable.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new SolicitudInvalidaException("El responsable del curso debe tener rol ADMINISTRADOR");
        }
        return responsable;
    }

    private List<Categoria> obtenerCategorias(List<UUID> categoriaIds) {
        return categoriaIds.stream()
                .map(id -> categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Categoría no encontrada con id: " + id)))
                .toList();
    }

    private static String resolverSlug(String slug, String titulo) {
        String origen = slug == null || slug.isBlank() ? titulo : slug;
        return SlugUtil.generar(origen);
    }

    private void validarSlugDisponible(String slug) {
        if (cursoRepository.existsBySlug(slug)) {
            throw new ConflictoException("Ya existe un curso con el slug: " + slug);
        }
    }

    private void validarSlugDisponibleParaOtroCurso(String slug, UUID id) {
        if (cursoRepository.existsBySlugAndIdNot(slug, id)) {
            throw new ConflictoException("Ya existe otro curso con el slug: " + slug);
        }
    }
}
