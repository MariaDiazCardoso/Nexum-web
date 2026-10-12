package com.nexum.dto;

public record ProgresoLeccionPatchRequest(
        Boolean completado,
        Integer ultimoSegundoVisto,
        Integer tiempoActivo
) {}
