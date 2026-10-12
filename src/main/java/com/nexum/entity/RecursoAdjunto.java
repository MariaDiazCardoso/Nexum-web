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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "recurso_adjunto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecursoAdjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leccion_id", nullable = false)
    private Leccion leccion;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "url_archivo", nullable = false, length = 255)
    private String urlArchivo;

    @Column(name = "tipo_archivo", length = 50)
    private String tipoArchivo;
}
