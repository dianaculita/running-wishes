package com.project.services.competition;

import com.project.converters.DtoToModel;
import com.project.converters.ModelToDto;
import com.project.dtos.CompetitionDto;
import com.project.models.Competition;
import com.project.models.Sport;
import com.project.repositories.CompetitionRepository;
import com.project.services.sport.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    private final SportService sportService;

    @Autowired
    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                  SportService sportService) {
        this.competitionRepository = competitionRepository;
        this.sportService = sportService;
    }

    @Override
    public CompetitionDto getCompetitionById(Long id) {
        return ModelToDto.competitionToDto(getById(id));
    }

    private Competition getById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<CompetitionDto> getAllCompetitions() {
        return competitionRepository.findAll().stream()
                .map(ModelToDto::competitionToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long createNewCompetition(CompetitionDto competitionDto) {
        Competition competition = Competition.builder()
                .name(competitionDto.getName())
                .location(competitionDto.getLocation())
                .numberOfDays(competitionDto.getNumberOfDays())
                .ticketFee(competitionDto.getTicketFee())
                .raisedMoney(0.0)
                .sport(getSport(competitionDto))
                .build();

        return competitionRepository.save(competition).getCompetitionId();
    }

    private Sport getSport(CompetitionDto competitionDto) {
        return DtoToModel.fromSportDto(sportService.getSportById(competitionDto.getSportId()));
    }

    /*
        once a competition has been created, the ticket fee or practiced sport can not be changed, only
        the location or name
     */
    @Override
    public void updateCompetition(CompetitionDto competitionDto) {
        Competition competition = getById(competitionDto.getCompetitionId());

        competition.setName(competitionDto.getName());
        competition.setLocation(competitionDto.getLocation());

        competitionRepository.save(competition);
    }

    @Override
    public void deleteCompetition(Long id) {
        competitionRepository.delete(getById(id));
    }

}
