package com.nexum.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioMedallaRequest(
        @NotNull UUID medallaId
) {}
