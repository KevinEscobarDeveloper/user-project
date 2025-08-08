package com.tecnica.prueba.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name must have at least 2 characters")
    private String name;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, message = "Last name must have at least 2 characters")
    private String lastName;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be equal or greater than 0")
    private Integer age;

    @NotNull(message = "Birth date cannot be null")
    private LocalDate birthDate;

    @NotNull(message = "Password cannot be null")
    private String password;


}
