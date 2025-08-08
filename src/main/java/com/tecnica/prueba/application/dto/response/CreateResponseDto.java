package com.tecnica.prueba.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResponseDto<T> {
    private T data;
    private String method;
    private String url;
    private String message;
}
