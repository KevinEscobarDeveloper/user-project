package com.tecnica.prueba.domain.port.out;

import reactor.core.publisher.Flux;

public interface ClientRolePort {
    Flux<String> findRoleNamesByClientId(Long clientId);

}
