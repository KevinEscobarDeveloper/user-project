package com.tecnica.prueba.adapters.in;

import com.tecnica.prueba.application.dto.request.AuthRequest;
import com.tecnica.prueba.application.dto.response.AuthResponse;
import com.tecnica.prueba.domain.port.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Service", description = "Authentication Endpoint")
public class LoginController {

    private final AuthUseCase authUseCase;

    @Operation(
            summary = "Login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody @Valid AuthRequest request) {
        return authUseCase.login(request)
                .map(ResponseEntity::ok);
    }
}
