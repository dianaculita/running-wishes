package com.project.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.dtos.ParticipantDto;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.models.Sponsor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ParticipantMapper {

    @Mapping(target = "competitionsIds", source = "participantEntity.competitions", qualifiedByName = "extractCompetitions")
    ParticipantDto participantEntityToDto(Participant participantEntity);

    Participant participantDtoToEntity(ParticipantDto participantDto);

    @Named("extractCompetitions")
    default List<Long> extractCompetitions(List<Competition> competitions) {
        if (competitions == null) {
            return new ArrayList<>();
        }

        return competitions.stream()
                       .map(Competition::getCompetitionId)
                       .collect(Collectors.toUnmodifiableList());
    }
}
