package com.nexum.controller;

import com.nexum.dto.AuthTokenRequest;
import com.nexum.dto.AuthTokenResponse;
import com.nexum.dto.UsuarioRegistroRequest;
import com.nexum.dto.UsuarioResponse;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.UsuarioRepository;
import com.nexum.security.UsuarioDetails;
import com.nexum.service.JwtService;
import com.nexum.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrar(request));
    }

    @PostMapping("/token")
    public ResponseEntity<AuthTokenResponse> token(@Valid @RequestBody AuthTokenRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            normalizar(request.email()),
                            request.password()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Email o contraseña inválidos");
        }

        UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findById(usuarioDetails.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + usuarioDetails.getId()));
        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuarioDetails);
        return ResponseEntity.ok(new AuthTokenResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                UsuarioResponse.desde(usuario)));
    }

    private static String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
