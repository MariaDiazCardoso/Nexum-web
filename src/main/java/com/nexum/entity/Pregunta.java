package com.nexum.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pregunta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", nullable = false)
    private Examen examen;

    @Column(name = "texto_pregunta", nullable = false, columnDefinition = "text")
    private String textoPregunta;

    @Column(name = "explicacion_respuesta", columnDefinition = "text")
    private String explicacionRespuesta;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OpcionRespuesta> opciones = new ArrayList<>();

    public void agregarOpcion(OpcionRespuesta opcion) {
        opciones.add(opcion);
        opcion.setPregunta(this);
    }

    public void reemplazarOpciones(List<OpcionRespuesta> nuevasOpciones) {
        opciones.clear();
        nuevasOpciones.forEach(this::agregarOpcion);
    }
}
