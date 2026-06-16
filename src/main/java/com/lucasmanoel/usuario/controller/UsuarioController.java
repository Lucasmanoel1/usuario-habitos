package com.lucasmanoel.usuario.controller;

import com.lucasmanoel.usuario.business.UsuarioService;
import com.lucasmanoel.usuario.business.dto.request.LoginRequest;
import com.lucasmanoel.usuario.business.dto.request.RegisterUserRequest;
import com.lucasmanoel.usuario.business.dto.request.UsuarioRequest;
import com.lucasmanoel.usuario.business.dto.response.LoginResponse;
import com.lucasmanoel.usuario.business.dto.response.RegisterUserResponse;
import com.lucasmanoel.usuario.business.dto.response.UsuarioResponse;
import com.lucasmanoel.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Cadastro de usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário", description = "Cria um novo usuário no sistema e retorna os dados cadastrados.")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos.")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado no sistema.")
    public ResponseEntity<RegisterUserResponse> cadastrarUsuario(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrarUsuario(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Realiza o login do usuário e retorna o token de acesso.")
    @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso.")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @GetMapping
    @Operation(summary = "Buscar usuário por e-mail", description = "Busca as informações de um usuário específico através do e-mail.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.")
    @ApiResponse(responseCode = "401", description = "Token de autorização ausente ou inválido.")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    public ResponseEntity<UsuarioResponse> BuscarUsuarioPorEmail(@RequestHeader("Authorization") String token, @RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(token, email));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema baseado no e-mail informado.")
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso (sem corpo de resposta).")
    @ApiResponse(responseCode = "401", description = "Token de autorização ausente ou inválido.")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    public ResponseEntity<Void> deletarUsuario(@RequestHeader("Authorization") String token, @PathVariable String email) {
        usuarioService.deletarUsuario(token, email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping
    @Operation(summary = "Atualizar dados do usuário", description = "Altera as informações cadastrais do usuário.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos.")
    @ApiResponse(responseCode = "401", description = "Token de autorização ausente ou inválido.")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    public ResponseEntity<UsuarioResponse> alteraUsuario(@RequestHeader("Authorization") String token, @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok().body(usuarioService.alteraUsuario(token, request));
    }

}
