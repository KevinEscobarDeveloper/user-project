package com.tecnica.prueba.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String name;
    private String lastName;
    private Integer age;
    private LocalDate birthDate;
    private String password;
    private Long roleId;
}
