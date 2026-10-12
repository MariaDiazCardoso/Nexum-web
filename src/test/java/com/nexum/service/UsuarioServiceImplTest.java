package com.nexum.service;

import com.nexum.dto.*;
import com.nexum.entity.RolUsuario;
import com.nexum.entity.Usuario;
import com.nexum.exception.ConflictoException;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl — pruebas unitarias")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl service;

    private UUID id;
    private Usuario usuarioGuardado;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        usuarioGuardado = Usuario.builder()
                .id(id)
                .nombre("Ana")
                .apellido("López")
                .email("ana@nexum.com")
                .passwordHash("$2a$hash")
                .rol(RolUsuario.USUARIO)
                .puntosXp(0)
                .emailVerificado(false)
                .build();
    }

    // ── registrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registrar OK → devuelve UsuarioResponse sin passwordHash")
    void registrarOk() {
        given(usuarioRepository.existsByEmail("ana@nexum.com")).willReturn(false);
        given(passwordEncoder.encode("password1")).willReturn("$2a$hash");
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioGuardado);

        UsuarioResponse resp = service.registrar(
                new UsuarioRegistroRequest("Ana", "López", "ANA@nexum.com", "password1"));

        assertThat(resp.email()).isEqualTo("ana@nexum.com");  // normalizado
        assertThat(resp.rol()).isEqualTo(RolUsuario.USUARIO);
        then(usuarioRepository).should().save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrar con email duplicado → ConflictoException")
    void registrarEmailDuplicado() {
        given(usuarioRepository.existsByEmail(anyString())).willReturn(true);

        assertThatThrownBy(() -> service.registrar(
                new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1")))
                .isInstanceOf(ConflictoException.class);

        then(usuarioRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("registrar siempre fuerza rol USUARIO")
    void registrarFuerzaRolUsuario() {
        given(usuarioRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("hash");
        // Capturamos el argumento guardado
        given(usuarioRepository.save(any(Usuario.class))).willAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            assertThat(u.getRol()).isEqualTo(RolUsuario.USUARIO);
            return usuarioGuardado;
        });

        service.registrar(new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1"));
    }

    @Test
    @DisplayName("registrar codifica la contraseña con BCrypt")
    void registrarHashDistintoDePassword() {
        given(usuarioRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode("password1")).willReturn("$2a$diferente");
        given(usuarioRepository.save(any(Usuario.class))).willAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            assertThat(u.getPasswordHash()).isEqualTo("$2a$diferente");
            assertThat(u.getPasswordHash()).isNotEqualTo("password1");
            return usuarioGuardado;
        });

        service.registrar(new UsuarioRegistroRequest("Ana", "López", "ana@nexum.com", "password1"));
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId OK → devuelve UsuarioResponse")
    void obtenerOk() {
        given(usuarioRepository.findById(id)).willReturn(Optional.of(usuarioGuardado));

        UsuarioResponse resp = service.obtenerPorId(id);
        assertThat(resp.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("obtenerPorId inexistente → RecursoNoEncontradoException")
    void obtenerInexistente() {
        given(usuarioRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(UUID.randomUUID()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ── actualizar (PUT) ──────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar con email de otro usuario → ConflictoException")
    void actualizarEmailDeOtro() {
        given(usuarioRepository.findById(id)).willReturn(Optional.of(usuarioGuardado));
        given(usuarioRepository.existsByEmailAndIdNot("otro@nexum.com", id)).willReturn(true);

        assertThatThrownBy(() -> service.actualizar(id,
                new UsuarioRequest("Ana", "López", "otro@nexum.com", null)))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    @DisplayName("actualizar no modifica rol ni emailVerificado")
    void actualizarNoModificaRolNiVerificado() {
        given(usuarioRepository.findById(id)).willReturn(Optional.of(usuarioGuardado));
        given(usuarioRepository.existsByEmailAndIdNot(anyString(), eq(id))).willReturn(false);
        given(usuarioRepository.save(any(Usuario.class))).willAnswer(inv -> inv.getArgument(0));

        service.actualizar(id, new UsuarioRequest("Ana", "López", "ana@nexum.com", null));

        // El rol y emailVerificado del objeto guardado siguen siendo los originales
        assertThat(usuarioGuardado.getRol()).isEqualTo(RolUsuario.USUARIO);
        assertThat(usuarioGuardado.getEmailVerificado()).isFalse();
    }
}

