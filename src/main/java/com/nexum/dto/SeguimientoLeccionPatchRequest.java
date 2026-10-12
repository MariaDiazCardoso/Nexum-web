package com.nexum.dto;

import jakarta.validation.constraints.Min;

public record SeguimientoLeccionPatchRequest(

        @Min(value = 0, message = "El tiempo acumulado no puede ser negativo")
        Integer tiempoAcumulado,

        @Min(value = 0, message = "La cantidad de visitas no puede ser negativa")
        Integer cantidadVisitas,

        @Min(value = 0, message = "Los intentos de práctica no pueden ser negativos")
        Integer intentosPractica
) {}
