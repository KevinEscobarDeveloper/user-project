package com.tecnica.prueba.adapters.out.repository.mysql.client;

import com.tecnica.prueba.adapters.model.mysql.ClientRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ClientRoleRepository extends ReactiveCrudRepository<ClientRole, Long> {
    Flux<ClientRole> findAllByClientId(Long clientId);
    Flux<ClientRole> findAllByRoleId(Long roleId);
}
