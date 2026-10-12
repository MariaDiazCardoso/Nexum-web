package com.nexum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "inscripcion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inscripcion_usuario_curso",
                columnNames = {"usuario_id", "curso_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    public static final String ESTADO_ACTIVA = "ACTIVA";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = ESTADO_ACTIVA;

    @Column(name = "progreso_porcentaje", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progresoPorcentaje = BigDecimal.ZERO;

    @Column(name = "fecha_inscripcion", nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    public void prePersist() {
        if (estado == null || estado.isBlank()) {
            estado = ESTADO_ACTIVA;
        }
        if (progresoPorcentaje == null) {
            progresoPorcentaje = BigDecimal.ZERO;
        }
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
    }
}
