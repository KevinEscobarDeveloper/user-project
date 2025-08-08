package com.tecnica.prueba.domain.port.out;

import com.tecnica.prueba.application.dto.request.ClientDto;
import com.tecnica.prueba.domain.entity.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<Void> saveClient(ClientDto clientDto);
    Flux<Client> getAllClients();
    Mono<Client> findByUser(String username);
    Flux<Client> findPage(int size, int offset);
    Mono<Long> countAllClients();

}
