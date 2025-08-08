package com.tecnica.prueba.adapters.configuration.utils;

import com.tecnica.prueba.application.dto.response.CreateResponseDto;
import org.springframework.http.server.reactive.ServerHttpRequest;
public class ResponseBuilder {

    public static <T> CreateResponseDto<T> build(T data, ServerHttpRequest request) {
        request.getMethod();
        return CreateResponseDto.<T>builder()
                .data(data)
                .method(request.getMethod().name())
                .message("Created successfully")
                .url(request.getURI().toString())
                .build();
    }

}