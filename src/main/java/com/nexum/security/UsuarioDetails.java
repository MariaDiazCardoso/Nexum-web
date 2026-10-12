package com.nexum.security;

import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final RolUsuario rol;

    public UsuarioDetails(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.passwordHash = usuario.getPasswordHash();
        this.rol = usuario.getRol();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public RolUsuario getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
