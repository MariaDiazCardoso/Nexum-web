package com.nexum.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Manejador global de excepciones (T02).
 * Todas las respuestas de error usan {@link ErrorResponse}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 404 ──────────────────────────────────────────────────────────────────
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            RecursoNoEncontradoException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "No encontrado",
                        ex.getMessage(),
                        req.getRequestURI()));
    }

    // ── 409 ──────────────────────────────────────────────────────────────────
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> handleConflicto(
            ConflictoException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        "Conflicto",
                        ex.getMessage(),
                        req.getRequestURI()));
    }

    // ── 400 (regla de negocio) ────────────────────────────────────────────────
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudInvalida(
            SolicitudInvalidaException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Solicitud inválida",
                        ex.getMessage(),
                        req.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "No autenticado",
                        "Email o contraseña inválidos",
                        req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        "Acceso denegado",
                        "No tiene permisos para acceder a este recurso",
                        req.getRequestURI()));
    }

    // ── 400 (validación Jakarta — con detalle por campo) ──────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ErrorResponse.CampoError> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.CampoError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.ofValidacion(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validación fallida",
                        "Uno o más campos son inválidos",
                        req.getRequestURI(),
                        errores));
    }

    // ── 400 (body ilegible o UUID mal formado) ────────────────────────────────
    @ExceptionHandler({HttpMessageNotReadableException.class,
                        MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformed(
            Exception ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Solicitud mal formada",
                        "El cuerpo o un parámetro de la solicitud no tiene el formato esperado",
                        req.getRequestURI()));
    }

    // ── 409 (violación de integridad de BD) ──────────────────────────────────
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest req) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        "Conflicto de integridad",
                        "La operación viola una restricción de la base de datos",
                        req.getRequestURI()));
    }

    // ── 500 (cualquier otra excepción) ────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest req) {

        log.error("Error inesperado en {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno",
                        "Ocurrió un error inesperado. Contacte al administrador.",
                        req.getRequestURI()));
    }
}

