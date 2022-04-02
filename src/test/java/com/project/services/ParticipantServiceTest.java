package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.ParticipantDto;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.ParticipantRepository;
import com.project.services.participant.ParticipantServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DataJpaTest
public class ParticipantServiceTest {

    public static final String CNP = "2980804776699";
    public static final long COMPETITION_ID = 1L;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(participantRepository, competitionRepository);
    }

    @Test
    public void testGetParticipantByCnp() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        ParticipantDto expectedParticipant = ModelToDto.participantToDto(participant);

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.of(participant));

        ParticipantDto actualParticipant = participantService.getParticipantByCnp(CNP);

        verifyAssertions(expectedParticipant, actualParticipant);

        verify(participantRepository).findByCnp(anyString());
    }

    private void verifyAssertions(ParticipantDto expectedParticipant, ParticipantDto actualParticipant) {
        assertEquals(expectedParticipant.getName(), actualParticipant.getName());
        assertEquals(expectedParticipant.getCompetitionsIds(), actualParticipant.getCompetitionsIds());
    }

    private Participant getParticipantMock(String cnp, String name) {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Color Run");

        return Participant.builder()
                .cnp(cnp)
                .name(name)
                .participatesToCompetitions(Arrays.asList(competition))
                .build();
    }

    private Competition getCompetitionMock(Long id, String name) {
        return Competition.builder()
                .competitionId(id)
                .name(name)
                .ticketFee(30.0)
                .raisedMoney(30.0)
                .build();
    }

    @Test
    public void testGetParticipantByCnp_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(participantRepository).findByCnp(CNP);

        assertThrows(ResponseStatusException.class, () -> participantService.getParticipantByCnp(CNP));

        verify(participantRepository).findByCnp(anyString());
    }

    @Test
    public void testGetAllParticipants() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        Participant participant2 = getParticipantMock("1970804736699", "Daniel");
        List<Participant> participants = Arrays.asList(participant, participant2);

        ParticipantDto participantDto = ModelToDto.participantToDto(participant);
        ParticipantDto participantDto2 = ModelToDto.participantToDto(participant2);
        List<ParticipantDto> expectedParticipants = Arrays.asList(participantDto, participantDto2);

        when(participantRepository.findAll()).thenReturn(participants);

        List<ParticipantDto> actualParticipants = participantService.getAllParticipants();

        assertEquals(expectedParticipants.size(), actualParticipants.size());
        verifyAssertions(expectedParticipants.get(0), actualParticipants.get(0));
        verifyAssertions(expectedParticipants.get(1), actualParticipants.get(1));

        verify(participantRepository).findAll();
    }

    @Test
    public void testCreateNewParticipant() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        ParticipantDto participantDto = ModelToDto.participantToDto(participant);

        Competition competition = participant.getParticipatesToCompetitions().get(0);

        when(competitionRepository.findByCompetitionIdIn(participantDto.getCompetitionsIds())).thenReturn(Arrays.asList(competition));
        when(competitionRepository.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        participantService.createNewParticipant(participantDto);

        verify(competitionRepository).findByCompetitionIdIn(anyList());
        verify(competitionRepository).getById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
        verify(participantRepository).save(any(Participant.class));
    }

    @Disabled
    @Test
    public void testUpdateParticipant() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        ParticipantDto updatedParticipant = ParticipantDto.builder()
                .cnp(CNP)
                .name("Ioana")
                .competitionsIds(Arrays.asList(COMPETITION_ID, COMPETITION_ID + 1L))
                .build();

        Competition competition = getCompetitionMock(COMPETITION_ID, "Color Run");
        Competition competition2 = getCompetitionMock(COMPETITION_ID + 1L, "Run");
        List<Competition> competitions = Arrays.asList(competition, competition2);

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.of(participant));
        when(competitionRepository.findByCompetitionIdIn(updatedParticipant.getCompetitionsIds())).thenReturn(competitions);
        when(competitionRepository.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(competitionRepository.getById(COMPETITION_ID + 1L)).thenReturn(competition2);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition2);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        participantService.updateParticipant(updatedParticipant);

        verify(participantRepository).findByCnp(anyString());
        verify(competitionRepository).findByCompetitionIdIn(anyList());
        verify(competitionRepository, times(2)).getById(anyLong());
        verify(competitionRepository, times(2)).save(any(Competition.class));
        verify(participantRepository).save(any(Participant.class));
    }

    @Test
    public void testUpdateParticipant_shouldThrowNotFoundException() {
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setCnp(CNP);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(participantRepository).findByCnp(CNP);

        assertThrows(ResponseStatusException.class, () -> participantService.updateParticipant(participantDto));

        verify(participantRepository).findByCnp(anyString());
    }

    @Test
    public void testDeleteParticipant() {
        Participant participant = getParticipantMock(CNP, "Ioana");

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.ofNullable(participant));
        doNothing().when(participantRepository).delete(participant);

        participantService.deleteParticipant(CNP);

        verify(participantRepository).findByCnp(anyString());
        verify(participantRepository).delete(any(Participant.class));
    }

    @Test
    public void testDeleteParticipant_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(participantRepository).findByCnp(CNP);

        assertThrows(ResponseStatusException.class, () -> participantService.deleteParticipant(CNP));

        verify(participantRepository).findByCnp(anyString());
    }

}
