package com.nexum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "leccion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_leccion_modulo_orden",
                columnNames = {"modulo_id", "orden"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(length = 20)
    private String tipo;

    @Column(nullable = false)
    private Integer orden;

    @Column(name = "contenido_texto", columnDefinition = "text")
    private String contenidoTexto;

    @Column(name = "puntos_otorgados")
    private Integer puntosOtorgados;

    @Column(name = "video_url", columnDefinition = "text")
    private String videoUrl;

    @Column(name = "xp_otorgada")
    private Integer xpOtorgada;

    @Column(length = 180)
    private String slug;

    @Column(name = "reto_practico", columnDefinition = "text")
    private String retoPractico;

    @Column(length = 20)
    private String dificultad;

    @Column(name = "duracion_estimada", length = 30)
    private String duracionEstimada;

    @Column(name = "duracion_minima")
    private Integer duracionMinima;

    @Column(name = "imagen_url", columnDefinition = "text")
    private String imagenUrl;
}
