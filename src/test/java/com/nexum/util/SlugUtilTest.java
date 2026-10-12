package com.nexum.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SlugUtil — generación de slugs")
class SlugUtilTest {

    @Test
    @DisplayName("Texto simple en minúsculas")
    void textoSimple() {
        assertThat(SlugUtil.generar("curso")).isEqualTo("curso");
    }

    @Test
    @DisplayName("Tildes y acentos se eliminan")
    void tildes() {
        assertThat(SlugUtil.generar("Introducción")).isEqualTo("introduccion");
        assertThat(SlugUtil.generar("café")).isEqualTo("cafe");
        assertThat(SlugUtil.generar("ángel")).isEqualTo("angel");
    }

    @Test
    @DisplayName("Espacios se convierten en guiones")
    void espacios() {
        assertThat(SlugUtil.generar("Diseño UX")).isEqualTo("diseno-ux");
    }

    @Test
    @DisplayName("Mayúsculas se convierten a minúsculas")
    void mayusculas() {
        assertThat(SlugUtil.generar("JAVA AVANZADO")).isEqualTo("java-avanzado");
    }

    @Test
    @DisplayName("Símbolos especiales se eliminan o convierten en guion")
    void simbolos() {
        assertThat(SlugUtil.generar("C++ y Java!")).isEqualTo("c-y-java");
        assertThat(SlugUtil.generar("precio: $100")).isEqualTo("precio-100");
    }

    @Test
    @DisplayName("Guiones múltiples se colapsan en uno")
    void guionesMultiples() {
        assertThat(SlugUtil.generar("a   b")).isEqualTo("a-b");
    }

    @Test
    @DisplayName("Guiones al inicio y fin se eliminan")
    void guionesExtremos() {
        assertThat(SlugUtil.generar("  !hola!  ")).isEqualTo("hola");
    }

    @Test
    @DisplayName("Texto null devuelve cadena vacía")
    void nulo() {
        assertThat(SlugUtil.generar(null)).isEmpty();
    }

    @Test
    @DisplayName("Texto en blanco devuelve cadena vacía")
    void blanco() {
        assertThat(SlugUtil.generar("   ")).isEmpty();
    }

    @Test
    @DisplayName("Texto complejo combinado")
    void combinado() {
        assertThat(SlugUtil.generar("Introducción al Diseño UX"))
                .isEqualTo("introduccion-al-diseno-ux");
    }
}

