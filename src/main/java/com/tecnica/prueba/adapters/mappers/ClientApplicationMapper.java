package com.tecnica.prueba.adapters.mappers;

import com.tecnica.prueba.application.dto.response.ClientDtoResponse;
import com.tecnica.prueba.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientApplicationMapper {
    ClientApplicationMapper INSTANCE = Mappers.getMapper(ClientApplicationMapper.class);

    ClientDtoResponse toClientResponse(Client dto);
    ClientDtoResponse toClientFromDomain(Client dto);

}
