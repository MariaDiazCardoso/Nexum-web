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
import com.nexum.repository.ModuloRepository;
import com.nexum.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("CursoServiceImpl — pruebas unitarias")
class CursoServiceImplTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ModuloRepository moduloRepository;

    @InjectMocks
    private CursoServiceImpl service;

    private UUID cursoId;
    private UUID responsableId;
    private UUID categoriaId;
    private Usuario administrador;
    private Categoria categoria;
    private Curso curso;

    @BeforeEach
    void setUp() {
        cursoId = UUID.randomUUID();
        responsableId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        administrador = Usuario.builder()
                .id(responsableId)
                .nombre("Ada")
                .apellido("Admin")
                .email("admin@nexum.com")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        categoria = Categoria.builder()
                .id(categoriaId)
                .nombre("Backend")
                .build();

        curso = Curso.builder()
                .id(cursoId)
                .titulo("Java Profesional")
                .slug("java-profesional")
                .responsable(administrador)
                .precio(new BigDecimal("49.90"))
                .estado(EstadoCurso.BORRADOR)
                .categorias(List.of(categoria))
                .build();
    }

    @Test
    @DisplayName("crear OK genera slug, fuerza estado BORRADOR y devuelve DTO")
    void crearOk() {
        given(usuarioRepository.findById(responsableId)).willReturn(Optional.of(administrador));
        given(cursoRepository.existsBySlug("java-profesional")).willReturn(false);
        given(categoriaRepository.findById(categoriaId)).willReturn(Optional.of(categoria));
        given(cursoRepository.save(any(Curso.class))).willAnswer(inv -> {
            Curso guardado = inv.getArgument(0);
            guardado.setId(cursoId);
            return guardado;
        });

        CursoResponse response = service.crear(request(null, null));

        assertThat(response.slug()).isEqualTo("java-profesional");
        assertThat(response.estado()).isEqualTo(EstadoCurso.BORRADOR);
        assertThat(response.responsableId()).isEqualTo(responsableId);
        assertThat(response.categoriaIds()).containsExactly(categoriaId);
    }

    @Test
    @DisplayName("crear con responsable inexistente lanza RecursoNoEncontradoException")
    void crearResponsableInexistente() {
        given(usuarioRepository.findById(responsableId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request(null, null)))
                .isInstanceOf(RecursoNoEncontradoException.class);

        then(cursoRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("crear con responsable no administrador lanza SolicitudInvalidaException")
    void crearResponsableNoAdministrador() {
        Usuario usuario = Usuario.builder()
                .id(responsableId)
                .rol(RolUsuario.USUARIO)
                .build();
        given(usuarioRepository.findById(responsableId)).willReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.crear(request(null, null)))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    @DisplayName("crear con categoría inexistente lanza RecursoNoEncontradoException")
    void crearCategoriaInexistente() {
        given(usuarioRepository.findById(responsableId)).willReturn(Optional.of(administrador));
        given(cursoRepository.existsBySlug("java-profesional")).willReturn(false);
        given(categoriaRepository.findById(categoriaId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request(null, null)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crear con slug duplicado lanza ConflictoException")
    void crearSlugDuplicado() {
        given(usuarioRepository.findById(responsableId)).willReturn(Optional.of(administrador));
        given(cursoRepository.existsBySlug("java-profesional")).willReturn(true);

        assertThatThrownBy(() -> service.crear(request(null, null)))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    @DisplayName("PATCH de estado actualiza solo el estado")
    void patchEstado() {
        given(cursoRepository.findById(cursoId)).willReturn(Optional.of(curso));
        given(cursoRepository.save(any(Curso.class))).willAnswer(inv -> inv.getArgument(0));

        CursoResponse response = service.actualizarParcial(
                cursoId,
                new CursoPatchRequest(null, null, null, null, null, null,
                        EstadoCurso.PUBLICADO, null, null, null, null, null));

        assertThat(response.estado()).isEqualTo(EstadoCurso.PUBLICADO);
        assertThat(response.titulo()).isEqualTo("Java Profesional");
    }

    @Test
    @DisplayName("eliminar con módulos asociados lanza ConflictoException")
    void eliminarConModulos() {
        given(cursoRepository.findById(cursoId)).willReturn(Optional.of(curso));
        given(moduloRepository.existsByCurso_Id(cursoId)).willReturn(true);

        assertThatThrownBy(() -> service.eliminar(cursoId))
                .isInstanceOf(ConflictoException.class);
    }

    private CursoRequest request(String slug, EstadoCurso estado) {
        return new CursoRequest(
                "Java Profesional",
                slug,
                "Curso completo",
                responsableId,
                new BigDecimal("49.90"),
                "INTERMEDIO",
                estado,
                "Desarrolladores",
                "8 semanas",
                "backend",
                "Programación",
                List.of(categoriaId)
        );
    }
}
