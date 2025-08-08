package com.tecnica.prueba.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String user;
    @NotBlank
    private String password;
}
