package com.nexum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Curso (D1 → tabla "curso").
 *
 * <p>La relación con {@link Categoria} se implementa como {@code @ManyToMany}
 * sobre la tabla intermedia {@code curso_categoria} (T05).
 * Las categorías se consultan a través del repositorio; la relación es
 * unidireccional desde Curso hacia Categoria.
 */
@Entity
@Table(name = "curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "text")
    private String descripcion;

    /** Responsable debe tener rol ADMINISTRADOR (⚠ supuesto aplicado — T05). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 20)
    private String nivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoCurso estado = EstadoCurso.BORRADOR;

    /** (RO) Calculado externamente; no se acepta en solicitudes. */
    @Column(name = "promedio_valoracion", precision = 3, scale = 2)
    private BigDecimal promedioValoracion;

    /** (RO) Asignado en @PrePersist. */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "recomendado_para", columnDefinition = "text")
    private String recomendadoPara;

    @Column(name = "duracion_estimada", length = 50)
    private String duracionEstimada;

    @Column(length = 50)
    private String ruta;

    @Column(name = "categoria_academica", length = 80)
    private String categoriaAcademica;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "curso_categoria",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private List<Categoria> categorias = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoCurso.BORRADOR;
        }
    }
}

