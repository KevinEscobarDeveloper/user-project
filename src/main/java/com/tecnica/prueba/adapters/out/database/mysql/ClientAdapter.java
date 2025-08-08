package com.tecnica.prueba.adapters.out.database.mysql;

import com.tecnica.prueba.adapters.mappers.ClientModelMapper;
import com.tecnica.prueba.adapters.model.mysql.ClientRole;
import com.tecnica.prueba.adapters.out.repository.mysql.client.ClientRepository;
import com.tecnica.prueba.adapters.out.repository.mysql.client.ClientRoleRepository;
import com.tecnica.prueba.domain.entity.Client;
import com.tecnica.prueba.domain.port.out.UserPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class ClientAdapter implements UserPort {
    private final ClientRepository clientRepository;
    private final ClientRoleRepository clientRoleRepository;

    @Override
    @Transactional
    public Mono<Void> saveClient(Client client) {
        return clientRepository.save(ClientModelMapper.INSTANCE.toModel(client))
                .flatMap(savedClient -> {
                    ClientRole rel = new ClientRole();
                    rel.setClientId(savedClient.getId());
                    rel.setRoleId(1L);
                    return clientRoleRepository.save(rel);
                })
                .then()
                .retry(3);
    }

    @Override
    public Mono<Client> findByUser(String username) {
        return clientRepository.findByName(username)
                .map(ClientModelMapper.INSTANCE::toDomain);
    }

    @Override
    public Flux<Client> findPage(int size, int offset) {
        return clientRepository.findPage(size, offset)
                .map(ClientModelMapper.INSTANCE::toDomain);
    }

    @Override
    public Mono<Long> countAllClients() {
        return clientRepository.countAll();
    }
}
