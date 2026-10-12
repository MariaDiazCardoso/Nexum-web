package com.nexum.service;

import com.nexum.repository.UsuarioRepository;
import com.nexum.security.UsuarioDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(normalizar(email))
                .map(UsuarioDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    private static String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
