package com.tecnica.prueba.adapters.out.database.mysql;

import com.tecnica.prueba.adapters.model.mysql.RoleModel;
import com.tecnica.prueba.adapters.out.repository.mysql.client.ClientRoleRepository;
import com.tecnica.prueba.adapters.out.repository.mysql.client.RoleRepository;
import com.tecnica.prueba.domain.port.out.ClientRolePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ClientRoleAdapter implements ClientRolePort {

    private final ClientRoleRepository clientRoleRepository;
    private final RoleRepository roleRepository;


    @Override
    public Flux<String> findRoleNamesByClientId(Long clientId) {
        return clientRoleRepository.findAllByClientId(clientId)
                .flatMap(clientRole -> roleRepository.findById(clientRole.getRoleId()))
                .map(RoleModel::getName);
    }
}
