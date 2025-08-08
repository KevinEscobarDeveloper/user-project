package com.tecnica.prueba.domain.port.in;

import com.tecnica.prueba.application.dto.request.AuthRequest;
import com.tecnica.prueba.application.dto.response.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<AuthResponse> login(AuthRequest request);
}
