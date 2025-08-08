package com.tecnica.prueba.adapters.configuration.filters;

import com.tecnica.prueba.adapters.configuration.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Value("${authenticate.url}")
    private String authenticateUrl;

    @Value("${refresh.url}")
    private String refreshUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();


        if (path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui.html")
                || path.equals("/swagger-ui")
                || (path.equals("/auth") && exchange.getRequest().getMethod() == HttpMethod.POST)
                || (path.equals("/auth/refresh") && exchange.getRequest().getMethod() == HttpMethod.POST)) {
            return chain.filter(exchange);
        }

        if (path.equals(authenticateUrl) || path.equals(refreshUrl)) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            log.warn("[JWT] Missing Authorization header");
            return unauthorized(exchange, "Missing Authorization header");
        }

        String compact = JwtUtil.compactToken(header);
        if (compact.isBlank()) {
            log.warn("[JWT] Invalid Authorization header format: {}", header);
            return unauthorized(exchange, "Invalid Authorization header format");
        }

        return jwtUtil.parseClaims(compact)
                .flatMap(claims -> {
                    Date exp = claims.getExpiration();
                    if (exp != null && exp.before(new Date())) {
                        log.warn("[JWT] Token expired: sub={}, exp={}", claims.getSubject(), exp);
                        return unauthorized(exchange, "Token expired");
                    }

                    Set<String> roles = jwtUtil.rolesFromClaims(claims);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                            .toList();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

                    log.debug("[JWT] Token valid: sub={}, roles={}, exp={}", claims.getSubject(), roles, exp);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .onErrorResume(e -> {
                    log.warn("[JWT] Token parsing failed: type={}, message={}", e.getClass().getSimpleName(), e.getMessage());
                    return unauthorized(exchange, "Invalid token: " + e.getMessage());
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(message.getBytes(StandardCharsets.UTF_8))));
    }
}
