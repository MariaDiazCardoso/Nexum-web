package com.nexum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de Usuario — validaciones Jakarta")
class UsuarioDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // ── UsuarioRegistroRequest ─────────────────────────────────────────────────

    @Test
    @DisplayName("UsuarioRegistroRequest válido → sin violaciones")
    void registroValido() {
        var req = new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1");
        assertThat(validator.validate(req)).isEmpty();
    }

    @Test
    @DisplayName("UsuarioRegistroRequest sin nombre → violación en campo nombre")
    void registroSinNombre() {
        var req = new UsuarioRegistroRequest("", "López", "ana@nexum.com", "password1");
        Set<ConstraintViolation<UsuarioRegistroRequest>> v = validator.validate(req);
        assertThat(v).isNotEmpty();
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    @DisplayName("UsuarioRegistroRequest email inválido → violación en campo email")
    void registroEmailInvalido() {
        var req = new UsuarioRegistroRequest("Ana", "López", "no-es-email", "password1");
        Set<ConstraintViolation<UsuarioRegistroRequest>> v = validator.validate(req);
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("UsuarioRegistroRequest contraseña corta → violación en campo password")
    void registroPasswordCorta() {
        var req = new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "corta");
        Set<ConstraintViolation<UsuarioRegistroRequest>> v = validator.validate(req);
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals("password"));
    }

    // ── UsuarioRequest ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("UsuarioRequest válido → sin violaciones")
    void requestValido() {
        var req = new UsuarioRequest("Ana", "López", "ana@nexum.com", null);
        assertThat(validator.validate(req)).isEmpty();
    }

    @Test
    @DisplayName("UsuarioRequest sin apellido → violación en campo apellido")
    void requestSinApellido() {
        var req = new UsuarioRequest("Ana", "", "ana@nexum.com", null);
        Set<ConstraintViolation<UsuarioRequest>> v = validator.validate(req);
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals("apellido"));
    }

    // ── UsuarioPatchRequest ────────────────────────────────────────────────────

    @Test
    @DisplayName("UsuarioPatchRequest vacío (todos null) → sin violaciones (todos opcionales)")
    void patchVacio() {
        var req = new UsuarioPatchRequest(null, null, null, null);
        assertThat(validator.validate(req)).isEmpty();
    }

    @Test
    @DisplayName("UsuarioPatchRequest email inválido → violación en campo email")
    void patchEmailInvalido() {
        var req = new UsuarioPatchRequest(null, null, "no-es-email", null);
        Set<ConstraintViolation<UsuarioPatchRequest>> v = validator.validate(req);
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals("email"));
    }
}

