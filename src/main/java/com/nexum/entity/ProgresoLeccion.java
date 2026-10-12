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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "progreso_leccion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_progreso_leccion_usuario_leccion",
                columnNames = {"usuario_id", "leccion_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgresoLeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leccion_id", nullable = false)
    private Leccion leccion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(name = "ultimo_segundo_visto")
    private Integer ultimoSegundoVisto;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "tiempo_activo")
    private Integer tiempoActivo;

    @Column(name = "xp_otorgada_aplicada", nullable = false)
    @Builder.Default
    private Boolean xpOtorgadaAplicada = false;

    @PrePersist
    public void prePersist() {
        if (completado == null) {
            completado = false;
        }
        if (xpOtorgadaAplicada == null) {
            xpOtorgadaAplicada = false;
        }
    }
}
