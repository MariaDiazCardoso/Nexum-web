package com.nexum.controller;

import com.nexum.dto.*;
import com.nexum.entity.RolUsuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

/**
 * Prueba el controlador instanciando {@code new UsuarioController(servicioSimulado)}.
 * Sin contexto Spring ni BD.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController — pruebas unitarias")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private UsuarioController controller;
    private UUID id;
    private UsuarioResponse response;

    @BeforeEach
    void setUp() {
        controller = new UsuarioController(usuarioService);
        id = UUID.randomUUID();
        response = new UsuarioResponse(
                id, "Ana", "López", "ana@nexum.com",
                RolUsuario.USUARIO, 0, null,
                LocalDateTime.now(), null, false);
    }

    @Test
    @DisplayName("POST /api/usuarios → 201 con UsuarioResponse")
    void registrar201() {
        given(usuarioService.registrar(any())).willReturn(response);

        ResponseEntity<UsuarioResponse> resp = controller.registrar(
                new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().email()).isEqualTo("ana@nexum.com");
        then(usuarioService).should(times(1)).registrar(any());
    }

    @Test
    @DisplayName("POST /api/usuarios → 409 cuando email duplicado")
    void registrar409() {
        given(usuarioService.registrar(any())).willThrow(new ConflictoException("Duplicado"));

        org.junit.jupiter.api.Assertions.assertThrows(ConflictoException.class,
                () -> controller.registrar(
                        new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1")));
    }

    @Test
    @DisplayName("GET /api/usuarios → 200 con lista")
    void listar200() {
        given(usuarioService.listar()).willReturn(List.of(response));

        ResponseEntity<List<UsuarioResponse>> resp = controller.listar();

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} → 200")
    void obtener200() {
        given(usuarioService.obtenerPorId(id)).willReturn(response);

        ResponseEntity<UsuarioResponse> resp = controller.obtener(id);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().id()).isEqualTo(id);
        then(usuarioService).should(times(1)).obtenerPorId(id);
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} → 404 cuando no existe")
    void obtener404() {
        given(usuarioService.obtenerPorId(any()))
                .willThrow(new RecursoNoEncontradoException("No encontrado"));

        org.junit.jupiter.api.Assertions.assertThrows(RecursoNoEncontradoException.class,
                () -> controller.obtener(UUID.randomUUID()));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} → 200")
    void actualizar200() {
        given(usuarioService.actualizar(eq(id), any())).willReturn(response);

        ResponseEntity<UsuarioResponse> resp = controller.actualizar(id,
                new UsuarioRequest("Ana", "López", "ana@nexum.com", null));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(usuarioService).should(times(1)).actualizar(eq(id), any());
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id} → 200")
    void patch200() {
        given(usuarioService.actualizarParcial(eq(id), any())).willReturn(response);

        ResponseEntity<UsuarioResponse> resp = controller.actualizarParcial(id,
                new UsuarioPatchRequest(null, null, "nueva@nexum.com", null));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(usuarioService).should(times(1)).actualizarParcial(eq(id), any());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} → 204")
    void eliminar204() {
        willDoNothing().given(usuarioService).eliminar(id);

        ResponseEntity<Void> resp = controller.eliminar(id);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(usuarioService).should(times(1)).eliminar(id);
    }
}

