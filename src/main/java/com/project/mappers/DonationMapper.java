package com.project.mappers;

import com.project.dtos.DonationDto;
import com.project.models.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DonationMapper {

    @Mapping(target = "competitionId", source = "donationEntity.competition.competitionId")
    @Mapping(target = "charityPersonCnp", source = "donationEntity.charityPerson.personCnp")
    DonationDto donationEntityToDto(Donation donationEntity);

    Donation donationDtoToEntity(DonationDto donationDto);
}
