package com.nexum.service;

import com.nexum.dto.ModuloPatchRequest;
import com.nexum.dto.ModuloRequest;
import com.nexum.dto.ModuloResponse;
import com.nexum.entity.Curso;
import com.nexum.entity.Modulo;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.CursoRepository;
import com.nexum.repository.ModuloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuloServiceImpl — pruebas unitarias")
class ModuloServiceImplTest {

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private ModuloServiceImpl service;

    private UUID cursoId;
    private UUID moduloId;
    private Curso curso;
    private Modulo modulo;

    @BeforeEach
    void setUp() {
        cursoId = UUID.randomUUID();
        moduloId = UUID.randomUUID();
        curso = Curso.builder()
                .id(cursoId)
                .titulo("Java Profesional")
                .build();
        modulo = Modulo.builder()
                .id(moduloId)
                .curso(curso)
                .titulo("Fundamentos")
                .orden(1)
                .slug("fundamentos")
                .build();
    }

    @Test
    @DisplayName("crear OK genera slug y devuelve DTO")
    void crearOk() {
        given(cursoRepository.findById(cursoId)).willReturn(Optional.of(curso));
        given(moduloRepository.existsByCurso_IdAndOrden(cursoId, 1)).willReturn(false);
        given(moduloRepository.save(any(Modulo.class))).willAnswer(inv -> {
            Modulo guardado = inv.getArgument(0);
            guardado.setId(moduloId);
            return guardado;
        });

        ModuloResponse response = service.crear(new ModuloRequest(
                cursoId, "Fundamentos", 1, "Inicio", null));

        assertThat(response.cursoId()).isEqualTo(cursoId);
        assertThat(response.slug()).isEqualTo("fundamentos");
        assertThat(response.orden()).isEqualTo(1);
    }

    @Test
    @DisplayName("crear con curso inexistente lanza RecursoNoEncontradoException")
    void crearCursoInexistente() {
        given(cursoRepository.findById(cursoId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(new ModuloRequest(
                cursoId, "Fundamentos", 1, null, null)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crear con orden repetido por curso lanza ConflictoException")
    void crearOrdenDuplicado() {
        given(cursoRepository.findById(cursoId)).willReturn(Optional.of(curso));
        given(moduloRepository.existsByCurso_IdAndOrden(cursoId, 1)).willReturn(true);

        assertThatThrownBy(() -> service.crear(new ModuloRequest(
                cursoId, "Fundamentos", 1, null, null)))
                .isInstanceOf(ConflictoException.class);

        then(moduloRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("obtener inexistente lanza RecursoNoEncontradoException")
    void obtenerInexistente() {
        given(moduloRepository.findById(moduloId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(moduloId))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("PATCH de orden valida unicidad dentro del curso")
    void patchOrdenDuplicado() {
        given(moduloRepository.findById(moduloId)).willReturn(Optional.of(modulo));
        given(moduloRepository.existsByCurso_IdAndOrdenAndIdNot(cursoId, 2, moduloId))
                .willReturn(true);

        assertThatThrownBy(() -> service.actualizarParcial(
                moduloId,
                new ModuloPatchRequest(null, null, 2, null, null)))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    @DisplayName("eliminar con lecciones asociadas lanza ConflictoException")
    void eliminarConLecciones() {
        given(moduloRepository.findById(moduloId)).willReturn(Optional.of(modulo));
        given(moduloRepository.contarLeccionesPorModulo(moduloId)).willReturn(1L);

        assertThatThrownBy(() -> service.eliminar(moduloId))
                .isInstanceOf(ConflictoException.class);
    }
}
