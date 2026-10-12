package com.nexum.config;

import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            log.warn("ADMIN_EMAIL o ADMIN_PASSWORD no definidos; se omite el administrador inicial");
            return;
        }

        String emailNormalizado = adminEmail.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            log.info("Administrador inicial omitido: ya existe el email {}", emailNormalizado);
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .apellido("Nexum")
                .email(emailNormalizado)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .rol(RolUsuario.ADMINISTRADOR)
                .emailVerificado(true)
                .build();

        usuarioRepository.save(admin);
        log.info("Administrador inicial creado con email {}", emailNormalizado);
    }
}
