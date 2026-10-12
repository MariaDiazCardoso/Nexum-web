package com.nexum.dto;

public record AuthTokenResponse(
        String token,
        String tipo,
        long expiraEn,
        UsuarioResponse usuario
) {}
