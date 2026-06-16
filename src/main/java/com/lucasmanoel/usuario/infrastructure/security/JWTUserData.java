package com.lucasmanoel.usuario.infrastructure.security;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
