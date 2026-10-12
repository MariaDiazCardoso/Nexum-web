package com.nexum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de Modulo — validaciones Jakarta")
class ModuloDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("ModuloRequest válido no tiene violaciones")
    void requestValido() {
        ModuloRequest request = new ModuloRequest(UUID.randomUUID(), "Fundamentos", 1, null, null);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    @DisplayName("ModuloRequest sin título viola campo titulo")
    void requestSinTitulo() {
        ModuloRequest request = new ModuloRequest(UUID.randomUUID(), "", 1, null, null);

        Set<ConstraintViolation<ModuloRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("titulo"));
    }

    @Test
    @DisplayName("ModuloRequest con orden cero viola campo orden")
    void requestOrdenCero() {
        ModuloRequest request = new ModuloRequest(UUID.randomUUID(), "Fundamentos", 0, null, null);

        Set<ConstraintViolation<ModuloRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("orden"));
    }

    @Test
    @DisplayName("ModuloPatchRequest vacío no tiene violaciones")
    void patchVacio() {
        ModuloPatchRequest request = new ModuloPatchRequest(null, null, null, null, null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
