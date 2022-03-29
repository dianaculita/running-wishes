package com.project.services.participant;

import com.project.dtos.ParticipantDto;

import java.util.List;

public interface ParticipantService {

    ParticipantDto getParticipantByCnp(String cnp);

    List<ParticipantDto> getAllParticipants();

    String createNewParticipant(ParticipantDto participantDto);

    void updateParticipant(ParticipantDto participantDto);

    void deleteParticipant(String cnp);

}
