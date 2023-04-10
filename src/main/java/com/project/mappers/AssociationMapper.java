package com.project.mappers;

import com.project.dtos.AssociationDto;
import com.project.models.Association;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = CharityPersonMapper.class)
public interface AssociationMapper {

    AssociationDto associationEntityToDto(Association associationEntity);

    Association associationDtoToEntity(AssociationDto associationDto);
}
