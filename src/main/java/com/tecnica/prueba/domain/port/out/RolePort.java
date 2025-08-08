package com.tecnica.prueba.domain.port.out;

import reactor.core.publisher.Mono;

import java.util.Set;

public interface RolePort {
    Mono<Boolean> areRolesValid(Set<String> roles);
}
