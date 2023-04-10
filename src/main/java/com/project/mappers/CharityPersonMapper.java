package com.project.mappers;

import com.project.dtos.CharityPersonDto;
import com.project.models.CharityPerson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CharityPersonMapper {

    @Mapping(target = "associationId", source = "personEntity.association.associationId")
    CharityPersonDto personEntityToDto(CharityPerson personEntity);

    CharityPerson personDtoToEntity(CharityPersonDto personDto);
}
