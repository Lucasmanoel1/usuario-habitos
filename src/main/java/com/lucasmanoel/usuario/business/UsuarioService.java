package com.lucasmanoel.usuario.business;

import com.lucasmanoel.usuario.business.dto.request.LoginRequest;
import com.lucasmanoel.usuario.business.dto.request.RegisterUserRequest;
import com.lucasmanoel.usuario.business.dto.request.UsuarioRequest;
import com.lucasmanoel.usuario.business.dto.response.LoginResponse;
import com.lucasmanoel.usuario.business.dto.response.RegisterUserResponse;
import com.lucasmanoel.usuario.business.dto.response.UsuarioResponse;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictException;
import com.lucasmanoel.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.lucasmanoel.usuario.infrastructure.exceptions.UnauthorizedException;
import com.lucasmanoel.usuario.infrastructure.repository.UsuarioRepository;
import com.lucasmanoel.usuario.infrastructure.security.TokenConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    String emailNaoEncontrado = "Email não encontrado";

    public RegisterUserResponse cadastrarUsuario(RegisterUserRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ConflictException("E-mail já cadastrado");
        }
        UsuarioEntity entity = new UsuarioEntity();
        entity.setEmail(request.email());
        entity.setPassword(passwordEncoder.encode(request.password()));
        entity.setUsername(request.username());

        usuarioRepository.save(entity);

        return new RegisterUserResponse(request.username(), request.email());
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();
        String token = tokenConfig.generateToken(usuarioEntity);

        return new LoginResponse(token);
    }

    public UsuarioResponse buscarUsuarioPorEmail(String token, String email) {
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );
        if (!tokenConfig.extrairEmailToken(token.substring(7)).equals(entity.getEmail())) {
            throw new UnauthorizedException("Usuario não autenticado");
        }
        return new UsuarioResponse(entity.getUsername(), entity.getEmail());
    }

    public void deletarUsuario(String token, String email) {
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );
        if (!tokenConfig.extrairEmailToken(token.substring(7)).equals(entity.getEmail())) {
            throw new UnauthorizedException("Usuario não autenticado");
        }
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioResponse alteraUsuario(String token, UsuarioRequest request){
        String email = tokenConfig.extrairEmailToken(token.substring(7));
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );

        if (StringUtils.hasText(request.username()) && !entity.getUsername().equals(request.username())) {
            entity.setUsername(request.username());
        }
        if (StringUtils.hasText(request.email()) &&!entity.getEmail().equals(request.email())) {
            entity.setEmail(request.email());
        }
        if (StringUtils.hasText(request.password()) &&
                !passwordEncoder.matches(request.password(), entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(request.password()));
        }
        usuarioRepository.save(entity);
        return new UsuarioResponse(entity.getUsername(), entity.getEmail());
    }
}
