package com.tecnica.prueba.adapters.in;

import com.tecnica.prueba.adapters.utils.ResponseBuilder;
import com.tecnica.prueba.application.dto.request.ClientDto;
import com.tecnica.prueba.application.dto.response.ClientDtoResponse;
import com.tecnica.prueba.application.dto.response.CreateResponseDto;
import com.tecnica.prueba.application.dto.response.PageResponse;
import com.tecnica.prueba.domain.port.in.UserUseCase;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/clients")
@AllArgsConstructor
@Tag(name = "Product Service", description = "Clients Microservice Handler")
public class ClientController {
    private UserUseCase userUseCase;

    @Operation(
            summary = "Save client",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client saved successfully")
            }
    )
    @PostMapping
    @Timed(value = "http.server.clients.create", description = "Time to create")
    public Mono<ResponseEntity<CreateResponseDto<ClientDtoResponse>>> createUser(
            @RequestBody @Valid ClientDto request,
            ServerHttpRequest httpRequest
    ) {
        return this.userUseCase.saveClient(request)
                .map(clientDto -> {
                    CreateResponseDto<ClientDtoResponse> response = ResponseBuilder.build(clientDto, httpRequest);
                    String location = "/clients/";
                    return ResponseEntity
                            .created(URI.create(location))
                            .body(response);
                });
    }

    @Operation(
            summary = "Get all clients by page",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get all clients")
            }
    )
    @GetMapping("/page")
    @Timed(value = "http.server.clients.list", description = "List clients")
    public Mono<ResponseEntity<PageResponse<ClientDtoResponse>>> getClientsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userUseCase.getClientsPage(page, size)
                .map(ResponseEntity::ok);
    }
}
