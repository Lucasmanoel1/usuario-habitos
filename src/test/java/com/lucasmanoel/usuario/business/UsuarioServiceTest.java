package com.lucasmanoel.usuario.business;

import com.lucasmanoel.usuario.business.dto.request.LoginRequest;
import com.lucasmanoel.usuario.business.dto.request.RegisterUserRequest;
import com.lucasmanoel.usuario.business.dto.request.UsuarioRequest;
import com.lucasmanoel.usuario.business.dto.response.LoginResponse;
import com.lucasmanoel.usuario.business.dto.response.RegisterUserResponse;
import com.lucasmanoel.usuario.business.dto.response.UsuarioResponse;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictException;
import com.lucasmanoel.usuario.infrastructure.repository.UsuarioRepository;
import com.lucasmanoel.usuario.infrastructure.security.TokenConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;



    @Mock
    TokenConfig tokenConfig;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Authentication authentication;

    @Mock
    AuthenticationManager authenticationManager;

    String token = "Bearer 324234324324sfdfsdf434343";
    String email = "lucas@gmail.com";
    String password = "123";

    UsuarioEntity entity = new UsuarioEntity(
            email,
            password,
            "lucasmaneol"
    );

    RegisterUserRequest request = new RegisterUserRequest(entity.getUsername(), email, password);

    @BeforeEach
    public void setUp() {

        lenient().when(tokenConfig.extrairEmailToken("324234324324sfdfsdf434343")).thenReturn(email);
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        RegisterUserResponse resultado = usuarioService.cadastrarUsuario(request);

        assertNotNull(resultado);
        assertEquals(email, resultado.email());

        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void deveLancarErroQuandoEmailExistir() {
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);
        assertThrows(ConflictException.class, () -> usuarioService.cadastrarUsuario(request));
    }
//
    @Test
    void deveFazerLoginComSucesso() {

        LoginRequest request = new LoginRequest(email, password);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(entity);
        when(tokenConfig.generateToken(any())).thenReturn(token);

        LoginResponse resultado = usuarioService.login(request);

        assertNotNull(resultado);
        assertEquals(token, resultado.token());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenConfig, times(1)).generateToken(any());
    }
//
    @Test
    void deveBuscarUsuarioPorEmailComSucesso() {

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(entity));

        UsuarioResponse resultado = usuarioService.buscarUsuarioPorEmail(token, email);

        assertEquals(email, resultado.email());
    }

    @Test
    void deveDeletarUsuarioPorEmailComSucesso() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(entity));

        usuarioService.deletarUsuario(token, email);
        verify(usuarioRepository, times(1)).deleteByEmail(email);
    }

    @Test
    void deveAlterarUsuarioComSenhaComSucesso() {

        UsuarioRequest outro = new UsuarioRequest(
                "test",
                "email@Test",
                "password123"
        );

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        usuarioService.alteraUsuario(token, outro);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(entity);
    }

}
