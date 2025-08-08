package com.tecnica.prueba.adapters.configuration.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:my-super-secret-key-which-should-be-very-long}")
    private String secret;

    @Value("${jwt.expiration:3600}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh.expiration:86400}")
    private long refreshTokenExpirationTime;

    private volatile SecretKey key;
    private final ObjectMapper objectMapper;

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String ROLES_KEY = "roles";

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private SecretKey key() {
        SecretKey k = this.key;
        if (k == null) {
            synchronized (this) {
                k = this.key;
                if (k == null) {
                    k = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    this.key = k;
                }
            }
        }
        return k;
    }

    public String generateAccessToken(String subject, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES_KEY, roles);
        claims.put("tokenType", ACCESS_TOKEN);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + accessTokenExpirationTime * 1000);
        return Jwts.builder()
                .claims().add(claims).subject(subject).issuedAt(now).expiration(expiration).and()
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + refreshTokenExpirationTime * 1000);
        return Jwts.builder()
                .subject(subject)
                .claim("tokenType", REFRESH_TOKEN)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key())
                .compact();
    }

    public Mono<Claims> parseClaims(String tokenOrHeader) {
        return Mono.fromCallable(() -> {
                    String compact = compactToken(tokenOrHeader);
                    String p = preview(compact);
                    if (compact.isBlank()) {
                        log.warn("[JWT] empty token after compact. raw={}", preview(tokenOrHeader));
                        throw new IllegalArgumentException("Empty token");
                    }
                    Claims c = Jwts.parser().verifyWith(key()).build()
                            .parseSignedClaims(compact)
                            .getPayload();
                    log.debug("[JWT] parsed ok: sub={}, exp={}, preview={}", c.getSubject(), c.getExpiration(), p);
                    return c;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.warn("[JWT] parse error: type={}, msg={}, preview={}",
                        e.getClass().getSimpleName(), e.getMessage(), preview(tokenOrHeader)));
    }

    public Mono<Boolean> validateToken(String tokenOrHeader) {
        return parseClaims(tokenOrHeader)
                .map(c -> c.getExpiration() == null || !c.getExpiration().before(new Date()))
                .doOnNext(ok -> { if (!ok) log.warn("[JWT] expired on validate: preview={}", preview(tokenOrHeader)); })
                .onErrorReturn(false);
    }

    public Set<String> rolesFromClaims(Claims c) {
        Object rolesObj = c.get(ROLES_KEY);
        if (rolesObj == null) return Collections.emptySet();
        return objectMapper.convertValue(
                rolesObj,
                objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class)
        );
    }

    public static String compactToken(String tokenOrHeaderValue) {
        log.debug("[JWT] Authorization header received: '{}'", tokenOrHeaderValue == null ? "null" : tokenOrHeaderValue);
        if (tokenOrHeaderValue == null) return "";
        String s = tokenOrHeaderValue.trim();
        s = s.replaceFirst("(?i)^Bearer$", "")
                .replaceFirst("(?i)^Bearer\\s+", "")
                .trim();
        if (s.isEmpty()) return "";
        int spaceIdx = s.indexOf(' ');
        if (spaceIdx > 0) s = s.substring(0, spaceIdx);
        return s;
    }

    private String preview(String token) {
        if (token == null) return "null";
        int len = token.length();
        return len <= 12 ? token : token.substring(0, 12) + "...";
    }
}
