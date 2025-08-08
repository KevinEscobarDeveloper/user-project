package com.tecnica.prueba.application.usercase;

import com.tecnica.prueba.adapters.configuration.utils.JwtUtil;
import com.tecnica.prueba.application.dto.request.AuthRequest;
import com.tecnica.prueba.application.dto.response.AuthResponse;
import com.tecnica.prueba.application.exeptions.CustomException;
import com.tecnica.prueba.domain.port.in.AuthUseCase;
import com.tecnica.prueba.domain.port.out.ClientRolePort;
import com.tecnica.prueba.domain.port.out.RolePort;
import com.tecnica.prueba.domain.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {
    private final UserPort userPort;
    private final ClientRolePort clientRolePort;
    private final RolePort rolePort;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<AuthResponse> login(AuthRequest request) {
        return userPort.findByUser(request.getUser())
                .switchIfEmpty(Mono.error(new CustomException("Invalid credentials", HttpStatus.UNAUTHORIZED.value(), 1001)))
                .flatMap(client -> {
                    if (!request.getPassword().equals(client.getPassword())) {
                        return Mono.error(new CustomException("Invalid credentials", HttpStatus.UNAUTHORIZED.value(), 1001));
                    }
                    return clientRolePort.findRoleNamesByClientId(client.getId())
                            .collectList()
                            .flatMap(roles -> {
                                if (roles.isEmpty()) {
                                    return Mono.error(new CustomException("User has no roles", HttpStatus.FORBIDDEN.value(), 1002));
                                }
                                String token = jwtUtil.generateAccessToken(client.getName(), Set.copyOf(roles));
                                String refresh = jwtUtil.generateRefreshToken(client.getName());
                                return Mono.just(new AuthResponse(token, refresh, client.getName(), Set.copyOf(roles)));
                            });
                });
    }
}
