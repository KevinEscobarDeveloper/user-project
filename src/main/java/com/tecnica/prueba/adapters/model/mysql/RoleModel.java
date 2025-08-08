package com.tecnica.prueba.adapters.model.mysql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("role")
public class RoleModel {
    @Id
    private Long id;
    private String name;
}

