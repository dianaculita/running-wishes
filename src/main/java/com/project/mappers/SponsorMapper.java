package com.project.mappers;

import com.project.dtos.SponsorDto;
import com.project.models.Sponsor;
import org.mapstruct.Mapper;

@Mapper
public interface SponsorMapper {

    SponsorDto sponsorEntityToDto(Sponsor sponsorEntity);

    Sponsor sponsorDtoToEntity(SponsorDto sponsorDto);

}
