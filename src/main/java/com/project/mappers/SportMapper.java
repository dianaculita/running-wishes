package com.project.mappers;

import com.project.dtos.SportDto;
import com.project.models.Sport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper //(componentModel = "spring")
public interface SportMapper {

    SportDto sportEntityToDto(Sport sportEntity);

    Sport sportDtoToEntity(SportDto sportDto);
}
