package com.nexum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Usuario (D1 → tabla "usuario").
 * Anotaciones: @Entity @Table @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder.
 * No se usa @Data (convención del proyecto).
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RolUsuario rol = RolUsuario.USUARIO;

    @Column(name = "puntos_xp", nullable = false)
    @Builder.Default
    private Integer puntosXp = 0;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;

    @Column(name = "email_verificado", nullable = false)
    @Builder.Default
    private Boolean emailVerificado = false;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (puntosXp == null) {
            puntosXp = 0;
        }
        if (emailVerificado == null) {
            emailVerificado = false;
        }
        if (rol == null) {
            rol = RolUsuario.USUARIO;
        }
    }
}