package com.project.services.competition;

import com.project.dtos.CompetitionDto;

import java.util.List;

public interface CompetitionService {

    CompetitionDto getCompetitionById(Long id);

    List<CompetitionDto> getAllCompetitions();

    Long createNewCompetition(CompetitionDto competitionDto);

    void updateCompetition(CompetitionDto competitionDto);

    void deleteCompetition(Long id);
}
