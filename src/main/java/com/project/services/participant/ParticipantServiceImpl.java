package com.project.services.participant;

import com.project.converters.ModelToDto;
import com.project.dtos.ParticipantDto;
import com.project.exceptions.ParticipantAlreadyEnrolledException;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
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

    /**
     * A participant can enroll to the application with/without initial competitions to participate to
     */
    @Override
    public String createNewParticipant(ParticipantDto participantDto) {
        Participant participant = Participant.builder()
                .cnp(participantDto.getCnp())
                .name(participantDto.getName())
                .build();

        participant = participantRepository.save(participant);
        updateCompetitionWithParticipant(participant, participantDto);

        return participant.getCnp();
    }

    /**
     * Updates the competition fundraising budget after adding a new participant
     */
    private void updateCompetitionWithParticipant(Participant participant, ParticipantDto participantDto) {
        final Participant participantFinal = participant;

        participantDto.getCompetitionsIds().forEach(competitionId -> {
            Competition competition = competitionRepository.findById(competitionId)
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (!competition.getParticipants().contains(participant)) {
                competition.getParticipants().add(participantFinal);
                competition.setRaisedMoney(competition.getRaisedMoney() + competition.getTicketFee());
                competitionRepository.save(competition);
            }
        });
    }

    /**
     * The participant can update his participation to competitions only by adding more competitions,
     * meaning that the already bought ticket is non-refundable
     * The new added competitions will have their fundraising budget updated
     */
    @Override
    public void updateParticipant(ParticipantDto participantDto) {
        Participant participant = getByCnp(participantDto.getCnp());
        participant.setName(participantDto.getName());

        List<Long> competitionsIds = participant.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        if (competitionsIds.containsAll(participantDto.getCompetitionsIds())) {
            throw new ParticipantAlreadyEnrolledException();
        }

        updateCompetitionWithParticipant(participant, participantDto);
        participantRepository.save(participant);
    }

    /**
     * If the participant to be deleted participates in any competition, then a Bad Request exception
     * is thrown as the withdrawal from a competition is not permitted
     * The deletion of the participant is only possible if the competition list if empty
     */
    @Override
    public void deleteParticipant(String cnp) {
        Participant participant = getByCnp(cnp);
        if (participant.getCompetitions().size() > 0) {
            throw new BadRequestException("The participant can not withdraw from a competition!");
        } else {
            participantRepository.delete(participant);
        }
    }

}
