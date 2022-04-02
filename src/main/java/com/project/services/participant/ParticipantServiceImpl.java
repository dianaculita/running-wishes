package com.project.services.participant;

import com.project.converters.ModelToDto;
import com.project.dtos.ParticipantDto;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

    private final CompetitionRepository competitionRepository;

    @Autowired
    public ParticipantServiceImpl(ParticipantRepository participantRepository,
                                  CompetitionRepository competitionRepository) {
        this.participantRepository = participantRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public ParticipantDto getParticipantByCnp(String cnp) {
        return ModelToDto.participantToDto(getByCnp(cnp));
    }

    private Participant getByCnp(String cnp) {
        return participantRepository.findByCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<ParticipantDto> getAllParticipants() {
        return participantRepository.findAll().stream()
                .map(ModelToDto::participantToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String createNewParticipant(ParticipantDto participantDto) {
        Participant participant = Participant.builder()
                .cnp(participantDto.getCnp())
                .name(participantDto.getName())
                .participatesToCompetitions(participatesToCompetitions(participantDto.getCompetitionsIds()))
                .build();

        updateCompetitionsFundraisingBudget(participantDto.getCompetitionsIds());

        return participantRepository.save(participant).getCnp();
    }

    private List<Competition> participatesToCompetitions(List<Long> ids) {
        return competitionRepository.findByCompetitionIdIn(ids);
    }

    private void updateCompetitionsFundraisingBudget(List<Long> competitions) {
        for (Long competitionId : competitions) {
            Competition competition = competitionRepository.getById(competitionId);

            Double existingFunds = competition.getRaisedMoney();
            competition.setRaisedMoney(existingFunds + competition.getTicketFee());

            competitionRepository.save(competition);
        }
    }

    /**
     * The participant can update his participation to competitions only by adding more competitions,
     * meaning that the already bought ticket is non-refundable and the user can not withdraw
     * from a competition
     * The new added competitions will have their fundraising budget updated
     */
    @Override
    public void updateParticipant(ParticipantDto participantDto) {
        Participant participant = getByCnp(participantDto.getCnp());
        List<Long> oldCompetitions = participant.getParticipatesToCompetitions().stream()
                        .map(Competition::getCompetitionId)
                        .collect(Collectors.toList());

        List<Long> updatedCompetitions = participantDto.getCompetitionsIds();

        updatedCompetitions.removeAll(oldCompetitions); // finds only the newly added competitions in order to update
                                                        // their fundraising budget with the ticket price
        updateCompetitionsFundraisingBudget(updatedCompetitions);

        oldCompetitions.addAll(updatedCompetitions); // updates the list of competitions on top of the already existing ones
        participant.setName(participantDto.getName());
        participant.setParticipatesToCompetitions(participatesToCompetitions(oldCompetitions));

        participantRepository.save(participant);
    }

    @Override
    public void deleteParticipant(String cnp) {
        participantRepository.delete(getByCnp(cnp));
    }

}
