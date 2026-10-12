package com.nexum.controller;

import com.nexum.dto.ModuloPatchRequest;
import com.nexum.dto.ModuloRequest;
import com.nexum.dto.ModuloResponse;
import com.nexum.service.ModuloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuloController — pruebas unitarias")
class ModuloControllerTest {

    @Mock
    private ModuloService moduloService;

    private ModuloController controller;
    private UUID cursoId;
    private UUID moduloId;
    private ModuloResponse response;

    @BeforeEach
    void setUp() {
        controller = new ModuloController(moduloService);
        cursoId = UUID.randomUUID();
        moduloId = UUID.randomUUID();
        response = new ModuloResponse(moduloId, cursoId, "Fundamentos", 1, "Inicio", "fundamentos");
    }

    @Test
    @DisplayName("POST /api/modulos devuelve 201")
    void crear201() {
        given(moduloService.crear(any())).willReturn(response);

        ResponseEntity<ModuloResponse> result = controller.crear(request());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
        then(moduloService).should(times(1)).crear(any());
    }

    @Test
    @DisplayName("GET /api/modulos con cursoId devuelve 200")
    void listarPorCurso200() {
        given(moduloService.listar(cursoId)).willReturn(List.of(response));

        ResponseEntity<List<ModuloResponse>> result = controller.listar(cursoId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        then(moduloService).should(times(1)).listar(cursoId);
    }

    @Test
    @DisplayName("GET /api/modulos/{id} devuelve 200")
    void obtener200() {
        given(moduloService.obtenerPorId(moduloId)).willReturn(response);

        ResponseEntity<ModuloResponse> result = controller.obtener(moduloId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().id()).isEqualTo(moduloId);
    }

    @Test
    @DisplayName("PUT /api/modulos/{id} devuelve 200")
    void actualizar200() {
        given(moduloService.actualizar(eq(moduloId), any())).willReturn(response);

        ResponseEntity<ModuloResponse> result = controller.actualizar(moduloId, request());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(moduloService).should(times(1)).actualizar(eq(moduloId), any());
    }

    @Test
    @DisplayName("PATCH /api/modulos/{id} devuelve 200")
    void patch200() {
        given(moduloService.actualizarParcial(eq(moduloId), any())).willReturn(response);

        ResponseEntity<ModuloResponse> result = controller.actualizarParcial(
                moduloId,
                new ModuloPatchRequest(null, "Nuevo título", null, null, null));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(moduloService).should(times(1)).actualizarParcial(eq(moduloId), any());
    }

    @Test
    @DisplayName("DELETE /api/modulos/{id} devuelve 204")
    void eliminar204() {
        ResponseEntity<Void> result = controller.eliminar(moduloId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(moduloService).should(times(1)).eliminar(moduloId);
    }

    private ModuloRequest request() {
        return new ModuloRequest(cursoId, "Fundamentos", 1, "Inicio", null);
    }
}
