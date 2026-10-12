package com.nexum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad Categoria (D1 → tabla "categoria").
 * Representa un área de aprendizaje (D4).
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "icono_url", length = 255)
    private String iconoUrl;
}

