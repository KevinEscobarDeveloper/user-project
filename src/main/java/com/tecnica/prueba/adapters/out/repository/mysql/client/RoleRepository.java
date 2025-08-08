package com.tecnica.prueba.adapters.out.repository.mysql.client;

import com.tecnica.prueba.adapters.model.mysql.RoleModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<RoleModel, Long> {

    Mono<RoleModel> findByName(String name);
    Mono<RoleModel> findById(Long id);
}
