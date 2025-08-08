package com.tecnica.prueba.domain.port.in;

import com.tecnica.prueba.application.dto.response.ClientDtoResponse;
import com.tecnica.prueba.application.dto.response.PageResponse;
import com.tecnica.prueba.domain.entity.Client;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Mono<ClientDtoResponse> saveClient(Client client);
    Mono<PageResponse<ClientDtoResponse>> getClientsPage(int page, int size);
}
