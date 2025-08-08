package com.tecnica.prueba.adapters.out.database.mysql;

import com.tecnica.prueba.adapters.out.repository.mysql.client.RoleRepository;
import com.tecnica.prueba.domain.port.out.RolePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleAdapter implements RolePort {

    private final RoleRepository roleRepository;

    @Override
    public Mono<Boolean> areRolesValid(Set<String> roles) {
        return Flux.fromIterable(roles)
                .flatMap(roleRepository::findByName)
                .count()
                .map(count -> count == roles.size());
    }
}