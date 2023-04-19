package com.project.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.dtos.CompetitionDto;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.models.Sponsor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface CompetitionMapper {

    @Mapping(target = "sportId", source = "competitionEntity.sport.sportId")
    @Mapping(target = "sponsors", source = "competitionEntity.sponsors", qualifiedByName = "extractSponsors")
    @Mapping(target = "participantsCnp", source = "competitionEntity.participants", qualifiedByName = "extractParticipants")
    CompetitionDto competitionEntityToDto(Competition competitionEntity);

    @Named("extractSponsors")
    default List<String> extractSponsors(List<Sponsor> sponsors) {
        if (sponsors == null) {
            return new ArrayList<>();
        }

        return sponsors.stream()
              .map(Sponsor::getName)
              .collect(Collectors.toUnmodifiableList());
    }

    @Named("extractParticipants")
    default List<String> extractParticipants(List<Participant> participants) {
        if (participants == null) {
            return new ArrayList<>();
        }

        return participants.stream()
              .map(Participant::getCnp)
              .collect(Collectors.toUnmodifiableList());
    }


    //Competition competitionDtoToEntity(CompetitionDto competitionDto);
}
