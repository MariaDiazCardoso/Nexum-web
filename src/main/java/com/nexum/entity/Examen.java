package com.nexum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "examen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leccion_id", nullable = false, unique = true)
    private Leccion leccion;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(name = "intentos_maximos")
    private Integer intentosMaximos;

    @Column(name = "puntaje_minimo_aprobacion", precision = 5, scale = 2)
    private BigDecimal puntajeMinimoAprobacion;
}
