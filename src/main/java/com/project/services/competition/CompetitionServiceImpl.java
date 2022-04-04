package com.project.services.competition;

import com.project.converters.DtoToModel;
import com.project.converters.ModelToDto;
import com.project.dtos.CompetitionDto;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.models.Sponsor;
import com.project.models.Sport;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import com.project.services.sport.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    private final SportService sportService;

    private final DonationRepository donationRepository;

    @Autowired
    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                  SportService sportService,
                                  DonationRepository donationRepository) {
        this.competitionRepository = competitionRepository;
        this.sportService = sportService;
        this.donationRepository = donationRepository;
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

    /**
     * When a competition is created, it has no assigned participants or sponsors; they will be
     * updated later from requests made by the participants/sponors
     */
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

    /**
     * Once a competition has been created, the ticket fee or practiced sport can not be changed, only
     * the location or name
     */
    @Override
    public void updateCompetition(CompetitionDto competitionDto) {
        Competition competition = getById(competitionDto.getCompetitionId());

        competition.setName(competitionDto.getName());
        competition.setLocation(competitionDto.getLocation());

        competitionRepository.save(competition);
    }

    /**
     * When deleting a competition, sponsors will not be deleted
     * If a competition has already made donations, it can not be deleted, as the money
     * have already been sent
     */
    @Override
    public void deleteCompetition(Long id) {
        Competition competition = getById(id);
        List<Sponsor> sponsors = competition.getSponsors();
        List<Participant> participants = competition.getParticipants();
        competition.getSponsors().removeAll(sponsors);
        competition.getParticipants().removeAll(participants);
        competitionRepository.save(competition);

        if (competition.getDonations().size() > 0) {
            throw new BadRequestException("Competition can not be deleted as donations are in progress!");
        }

        competitionRepository.delete(competition);
    }

}
