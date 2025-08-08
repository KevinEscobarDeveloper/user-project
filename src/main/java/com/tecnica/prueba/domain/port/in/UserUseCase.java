package com.tecnica.prueba.domain.port.in;

import com.tecnica.prueba.application.dto.request.ClientDto;
import com.tecnica.prueba.application.dto.response.ClientDtoResponse;
import com.tecnica.prueba.application.dto.response.PageResponse;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Mono<ClientDtoResponse> saveClient(ClientDto client);
    Mono<PageResponse<ClientDtoResponse>> getClientsPage(int page, int size);
}
