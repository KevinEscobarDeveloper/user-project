package com.tecnica.prueba.domain.port.out;

import com.tecnica.prueba.domain.entity.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRolePort {
    Flux<Long> findRoleIdsByClientId(Long clientId);
    Flux<String> findRoleNamesByClientId(Long clientId);

}
