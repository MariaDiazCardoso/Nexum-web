package com.nexum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json(
                HttpStatus.UNAUTHORIZED.value(),
                "No autenticado",
                "Credenciales ausentes o inválidas",
                request.getRequestURI()));
    }

    private static String json(int status, String error, String message, String path) {
        return """
                {"timestamp":"%s","status":%d,"error":"%s","message":"%s","path":"%s","errores":null}
                """.formatted(LocalDateTime.now(), status, escape(error), escape(message), escape(path));
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
