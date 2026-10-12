package com.nexum.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler — respuestas de error")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.setRequestURI("/api/test");
        request = mock;
    }

    @Test
    @DisplayName("RecursoNoEncontradoException → 404")
    void notFound() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleNotFound(new RecursoNoEncontradoException("No existe"), request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().status()).isEqualTo(404);
        assertThat(resp.getBody().message()).isEqualTo("No existe");
        assertThat(resp.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("ConflictoException → 409")
    void conflicto() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleConflicto(new ConflictoException("Email duplicado"), request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("SolicitudInvalidaException → 400")
    void solicitudInvalida() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleSolicitudInvalida(new SolicitudInvalidaException("Regla violada"), request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("HttpMessageNotReadableException → 400")
    void malformed() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleMalformed(
                        new HttpMessageNotReadableException("bad json", (org.springframework.http.HttpInputMessage) null),
                        request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("DataIntegrityViolationException → 409")
    void dataIntegrity() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleDataIntegrity(
                        new DataIntegrityViolationException("constraint"), request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("Exception genérica → 500 sin exponer detalle")
    void general() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleGeneral(new RuntimeException("detalle interno"), request);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().status()).isEqualTo(500);
        // El mensaje al cliente no debe contener el detalle real
        assertThat(resp.getBody().message()).doesNotContain("detalle interno");
    }
}

