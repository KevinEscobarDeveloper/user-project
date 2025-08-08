package com.tecnica.prueba.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientDtoResponse {

    private String name;

    private String lastName;

    private Integer age;

    private LocalDate birthDate;

    private LocalDate estimatedDeathDate;
    private Integer yearsRemaining;
}