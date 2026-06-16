package com.lucasmanoel.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucasmanoel.usuario.business.UsuarioService;
import com.lucasmanoel.usuario.business.dto.request.LoginRequest;
import com.lucasmanoel.usuario.business.dto.request.RegisterUserRequest;
import com.lucasmanoel.usuario.business.dto.request.UsuarioRequest;
import com.lucasmanoel.usuario.business.dto.response.LoginResponse;
import com.lucasmanoel.usuario.business.dto.response.RegisterUserResponse;
import com.lucasmanoel.usuario.business.dto.response.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @InjectMocks
    UsuarioController usuarioController;

    @Mock
    UsuarioService usuarioService;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    final String token = "Bearer token_de_teste_123";
    String email = "lucas@gmail.com";

    RegisterUserRequest register = new RegisterUserRequest(
        "lucasmanoel",
            email,
            "123"

    );
    UsuarioResponse  response = new UsuarioResponse(
            "lucasmanoel",
            email
    );
    UsuarioRequest request = new UsuarioRequest(
            "lucasmanoel",
            email,
            "123"
    );
    LoginRequest loginRequest = new LoginRequest(
            email,
            "123"
    );
    LoginResponse  loginResponse = new LoginResponse(token);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(usuarioController)
                .build();
    }

    @Test
    void deveCadastrarUsuarioComSucessoERetornar201() throws Exception {
        when(usuarioService.cadastrarUsuario(register)).thenReturn(
                new RegisterUserResponse(register.username(), register.email()));

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
        verify(usuarioService).cadastrarUsuario(register);
    }

    @Test
    void deveAlterarUsuarioComSucessoERetornar200() throws Exception {
        when(usuarioService.alteraUsuario(token, request)).thenReturn(response);

        mockMvc.perform(put("/usuario")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(usuarioService).alteraUsuario(token, request);
    }

    @Test
    void deveBuscarUsuarioPorEmailComSucessoERetornar200() throws Exception {
        when(usuarioService.buscarUsuarioPorEmail(token, email)).thenReturn(response);

        mockMvc.perform(get("/usuario")
                        .header("Authorization", token)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(usuarioService).buscarUsuarioPorEmail(token, email);
    }

    @Test
    void deveFazerLoginComSucessoERetornar200() throws Exception {

        when(usuarioService.login(loginRequest)).thenReturn(loginResponse);

        mockMvc.perform(post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(loginResponse.token()));

        verify(usuarioService).login(loginRequest);
    }

    @Test
    void deveDeletarUsuarioPorEmailComSucessoERetornar204() throws Exception {

        mockMvc.perform(delete("/usuario/{email}", email)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        verify(usuarioService).deletarUsuario(token, email);
    }
}
