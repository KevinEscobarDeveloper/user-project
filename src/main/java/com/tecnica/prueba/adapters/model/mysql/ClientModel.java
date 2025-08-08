package com.tecnica.prueba.adapters.model.mysql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table("client_model")
public class ClientModel {
    @Id
    private Long id;
    private String name;
    private String lastName;
    private Integer age;
    private LocalDate birthDate;
    private String password;

}
