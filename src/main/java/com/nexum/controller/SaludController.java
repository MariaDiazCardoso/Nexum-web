package com.nexum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Ruta pública para que la plataforma de despliegue compruebe que el servicio está vivo.
 */
@RestController
@RequestMapping("/api/salud")
public class SaludController {

    // GET /api/salud → 200
    @GetMapping
    public ResponseEntity<Map<String, String>> estado() {
        return ResponseEntity.ok(Map.of("estado", "ok"));
    }
}
