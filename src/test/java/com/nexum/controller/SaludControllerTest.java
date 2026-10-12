package com.nexum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaludControllerTest {

    private final SaludController controller = new SaludController();

    @Test
    @DisplayName("GET /api/salud → 200 con estado ok")
    void estado_devuelve200() {
        ResponseEntity<Map<String, String>> respuesta = controller.estado();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("ok", respuesta.getBody().get("estado"));
    }
}
