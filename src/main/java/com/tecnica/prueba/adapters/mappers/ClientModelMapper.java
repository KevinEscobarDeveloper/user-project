package com.tecnica.prueba.adapters.mappers;

import com.tecnica.prueba.adapters.model.mysql.ClientModel;
import com.tecnica.prueba.application.dto.request.ClientDto;
import com.tecnica.prueba.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientModelMapper {
    ClientModelMapper INSTANCE = Mappers.getMapper(ClientModelMapper.class);

    ClientModel toModel(Client dto);
    Client toDomain(ClientModel dto);
    Client dtoToDomain(ClientDto dto);



}
