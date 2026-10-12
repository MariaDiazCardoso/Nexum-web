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
        name = "seguimiento_leccion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seguimiento_leccion_usuario_leccion",
                columnNames = {"usuario_id", "leccion_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoLeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leccion_id", nullable = false)
    private Leccion leccion;

    @Column(name = "tiempo_acumulado", nullable = false)
    @Builder.Default
    private Integer tiempoAcumulado = 0;

    @Column(name = "cantidad_visitas", nullable = false)
    @Builder.Default
    private Integer cantidadVisitas = 0;

    @Column(name = "intentos_practica", nullable = false)
    @Builder.Default
    private Integer intentosPractica = 0;

    @Column(name = "primera_actividad", nullable = false, updatable = false)
    private LocalDateTime primeraActividad;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @PrePersist
    public void prePersist() {
        if (tiempoAcumulado == null) {
            tiempoAcumulado = 0;
        }
        if (cantidadVisitas == null) {
            cantidadVisitas = 0;
        }
        if (intentosPractica == null) {
            intentosPractica = 0;
        }
        if (primeraActividad == null) {
            primeraActividad = LocalDateTime.now();
        }
    }
}
