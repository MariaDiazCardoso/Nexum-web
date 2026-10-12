package com.nexum.service;

import com.nexum.entity.RolUsuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.UsuarioRepository;
import com.nexum.security.UsuarioDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("accesoService")
@RequiredArgsConstructor
public class AccesoService {

    private final UsuarioRepository usuarioRepository;

    public void verificar(UUID usuarioId) {
        if (!esAdministrador() && !esPropio(usuarioId)) {
            throw new AccessDeniedException("Acceso denegado");
        }
    }

    public void verificarEmail(String email) {
        if (esAdministrador()) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioDetails details)
                || email == null || !details.getEmail().equalsIgnoreCase(email.trim())) {
            throw new AccessDeniedException("Acceso denegado");
        }
    }

    public void exigirAdministrador() {
        if (!esAdministrador()) {
            throw new AccessDeniedException("Acceso denegado");
        }
    }

    public boolean esAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    public boolean esPropio(UUID usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioDetails details)) {
            return false;
        }
        return details.getId().equals(usuarioId);
    }

    public UUID obtenerIdUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioDetails details) {
            return details.getId();
        }
        throw new AccessDeniedException("Acceso denegado");
    }

    public boolean esUsuarioAdministrador(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> usuario.getRol() == RolUsuario.ADMINISTRADOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + usuarioId));
    }
}
