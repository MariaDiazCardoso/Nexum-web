package com.nexum.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_medalla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioMedalla {

    @EmbeddedId
    private UsuarioMedallaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("medallaId")
    @JoinColumn(name = "medalla_id", nullable = false)
    private Medalla medalla;

    private LocalDateTime fechaObtencion;

    @PrePersist
    public void prePersist() {
        if (fechaObtencion == null) {
            fechaObtencion = LocalDateTime.now();
        }
    }
}
