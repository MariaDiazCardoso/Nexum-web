package com.nexum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de Curso — validaciones Jakarta")
class CursoDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("CursoRequest válido no tiene violaciones")
    void requestValido() {
        assertThat(validator.validate(request())).isEmpty();
    }

    @Test
    @DisplayName("CursoRequest sin título viola campo titulo")
    void requestSinTitulo() {
        CursoRequest request = new CursoRequest("", null, null, UUID.randomUUID(),
                BigDecimal.ZERO, null, null, null, null, null, null, List.of());

        Set<ConstraintViolation<CursoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("titulo"));
    }

    @Test
    @DisplayName("CursoRequest con precio negativo viola campo precio")
    void requestPrecioNegativo() {
        CursoRequest request = new CursoRequest("Java", null, null, UUID.randomUUID(),
                new BigDecimal("-1.00"), null, null, null, null, null, null, List.of());

        Set<ConstraintViolation<CursoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("precio"));
    }

    @Test
    @DisplayName("CursoRequest sin categoriaIds viola campo categoriaIds")
    void requestSinCategorias() {
        CursoRequest request = new CursoRequest("Java", null, null, UUID.randomUUID(),
                BigDecimal.ZERO, null, null, null, null, null, null, null);

        Set<ConstraintViolation<CursoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("categoriaIds"));
    }

    @Test
    @DisplayName("CursoPatchRequest vacío no tiene violaciones")
    void patchVacio() {
        CursoPatchRequest request = new CursoPatchRequest(null, null, null, null,
                null, null, null, null, null, null, null, null);

        assertThat(validator.validate(request)).isEmpty();
    }

    private CursoRequest request() {
        return new CursoRequest("Java Profesional", null, "Curso completo", UUID.randomUUID(),
                BigDecimal.ZERO, "INTERMEDIO", null, null, null, null, null, List.of());
    }
}
