package com.nexum.service;

import com.nexum.dto.CodigoVerificacionRequest;
import com.nexum.dto.CodigoVerificacionResponse;
import com.nexum.dto.CodigoVerificacionValidacionRequest;
import com.nexum.entity.CodigoVerificacion;
import com.nexum.entity.Usuario;
import com.nexum.exception.RecursoNoEncontradoException;
import com.nexum.exception.SolicitudInvalidaException;
import com.nexum.repository.CodigoVerificacionRepository;
import com.nexum.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodigoVerificacionServiceImpl implements CodigoVerificacionService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final CodigoVerificacionRepository codigoVerificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccesoService accesoService;

    @Override
    public CodigoVerificacionResponse generar(CodigoVerificacionRequest request) {
        accesoService.verificar(request.usuarioId());
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + request.usuarioId()));
        String codigoClaro = generarCodigoClaro();
        CodigoVerificacion codigo = CodigoVerificacion.builder()
                .usuario(usuario)
                .tipo(request.tipo())
                .codigoHash(passwordEncoder.encode(codigoClaro))
                .usado(false)
                .expiraEn(LocalDateTime.now().plusMinutes(request.minutosVigencia()))
                .build();
        return CodigoVerificacionResponse.desde(codigoVerificacionRepository.save(codigo), codigoClaro);
    }

    @Override
    @Transactional
    public CodigoVerificacionResponse validar(UUID id, CodigoVerificacionValidacionRequest request) {
        CodigoVerificacion codigo = buscarOLanzar(id);
        accesoService.verificar(codigo.getUsuario().getId());

        if (Boolean.TRUE.equals(codigo.getUsado())) {
            throw new SolicitudInvalidaException("El código de verificación ya fue usado");
        }
        if (codigo.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new SolicitudInvalidaException("El código de verificación está vencido");
        }
        if (!passwordEncoder.matches(request.codigo(), codigo.getCodigoHash())) {
            throw new SolicitudInvalidaException("El código de verificación no es válido");
        }

        codigo.setUsado(true);
        if (esVerificacionCorreo(codigo.getTipo())) {
            codigo.getUsuario().setEmailVerificado(true);
            usuarioRepository.save(codigo.getUsuario());
        }
        return CodigoVerificacionResponse.desde(codigoVerificacionRepository.save(codigo), null);
    }

    @Override
    public List<CodigoVerificacionResponse> listar(UUID usuarioId) {
        if (usuarioId != null) {
            accesoService.verificar(usuarioId);
            return codigoVerificacionRepository.findByUsuario_Id(usuarioId)
                    .stream()
                    .map(codigo -> CodigoVerificacionResponse.desde(codigo, null))
                    .toList();
        }
        accesoService.exigirAdministrador();
        return codigoVerificacionRepository.findAll()
                .stream()
                .map(codigo -> CodigoVerificacionResponse.desde(codigo, null))
                .toList();
    }

    private CodigoVerificacion buscarOLanzar(UUID id) {
        return codigoVerificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Código de verificación no encontrado con id: " + id));
    }

    private static String generarCodigoClaro() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private static boolean esVerificacionCorreo(String tipo) {
        return tipo != null && (tipo.equalsIgnoreCase("EMAIL")
                || tipo.equalsIgnoreCase("VERIFICACION_EMAIL")
                || tipo.equalsIgnoreCase("VERIFICACION_CORREO"));
    }
}
