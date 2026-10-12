package com.nexum.controller;

import com.nexum.dto.CursoPatchRequest;
import com.nexum.dto.CursoRequest;
import com.nexum.dto.CursoResponse;
import com.nexum.entity.EstadoCurso;
import com.nexum.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("CursoController — pruebas unitarias")
class CursoControllerTest {

    @Mock
    private CursoService cursoService;

    private CursoController controller;
    private UUID id;
    private UUID responsableId;
    private UUID categoriaId;
    private CursoResponse response;

    @BeforeEach
    void setUp() {
        controller = new CursoController(cursoService);
        id = UUID.randomUUID();
        responsableId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        response = new CursoResponse(
                id,
                "Java Profesional",
                "java-profesional",
                "Curso completo",
                responsableId,
                new BigDecimal("49.90"),
                "INTERMEDIO",
                EstadoCurso.BORRADOR,
                null,
                LocalDateTime.now(),
                "Desarrolladores",
                "8 semanas",
                "backend",
                "Programación",
                List.of(categoriaId)
        );
    }

    @Test
    @DisplayName("POST /api/cursos devuelve 201")
    void crear201() {
        given(cursoService.crear(any())).willReturn(response);

        ResponseEntity<CursoResponse> result = controller.crear(request());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
        then(cursoService).should(times(1)).crear(any());
    }

    @Test
    @DisplayName("GET /api/cursos con filtros devuelve 200")
    void listarConFiltros200() {
        given(cursoService.listar(categoriaId, EstadoCurso.BORRADOR)).willReturn(List.of(response));

        ResponseEntity<List<CursoResponse>> result = controller.listar(categoriaId, EstadoCurso.BORRADOR);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        then(cursoService).should(times(1)).listar(categoriaId, EstadoCurso.BORRADOR);
    }

    @Test
    @DisplayName("GET /api/cursos/{id} devuelve 200")
    void obtener200() {
        given(cursoService.obtenerPorId(id)).willReturn(response);

        ResponseEntity<CursoResponse> result = controller.obtener(id);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("PUT /api/cursos/{id} devuelve 200")
    void actualizar200() {
        given(cursoService.actualizar(eq(id), any())).willReturn(response);

        ResponseEntity<CursoResponse> result = controller.actualizar(id, request());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(cursoService).should(times(1)).actualizar(eq(id), any());
    }

    @Test
    @DisplayName("PATCH /api/cursos/{id} devuelve 200")
    void patch200() {
        given(cursoService.actualizarParcial(eq(id), any())).willReturn(response);

        ResponseEntity<CursoResponse> result = controller.actualizarParcial(
                id,
                new CursoPatchRequest(null, null, null, null, null, null,
                        EstadoCurso.PUBLICADO, null, null, null, null, null));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(cursoService).should(times(1)).actualizarParcial(eq(id), any());
    }

    @Test
    @DisplayName("DELETE /api/cursos/{id} devuelve 204")
    void eliminar204() {
        ResponseEntity<Void> result = controller.eliminar(id);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(cursoService).should(times(1)).eliminar(id);
    }

    private CursoRequest request() {
        return new CursoRequest(
                "Java Profesional",
                null,
                "Curso completo",
                responsableId,
                new BigDecimal("49.90"),
                "INTERMEDIO",
                null,
                "Desarrolladores",
                "8 semanas",
                "backend",
                "Programación",
                List.of(categoriaId)
        );
    }
}
