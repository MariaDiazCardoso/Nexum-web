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
@Table(name = "video_streaming")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoStreaming {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leccion_id", nullable = false, unique = true)
    private Leccion leccion;

    @Column(name = "aws_s3_key", nullable = false, length = 255)
    private String awsS3Key;

    @Column(name = "aws_cloudfront_url", length = 255)
    private String awsCloudfrontUrl;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    @Column(name = "calidad_disponible", length = 50)
    private String calidadDisponible;
}
