package com.nexum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado",
                "No tiene permisos para acceder a este recurso",
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
