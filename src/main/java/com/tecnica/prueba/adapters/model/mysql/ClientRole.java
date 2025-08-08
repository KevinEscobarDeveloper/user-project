package com.tecnica.prueba.adapters.model.mysql;



import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table("client_roles")
public class ClientRole{
    @Id
    private Long id;
    @Column("client_id")
    private Long clientId;
    @Column("role_id")
    private Long roleId;
}
