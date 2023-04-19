package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.BadRequestException;

import com.project.dtos.ParticipantDto;
import com.project.exceptions.ParticipantAlreadyEnrolledException;
import com.project.mappers.ParticipantMapper;
import com.project.mappers.ParticipantMapperImpl;
import com.project.models.Competition;
import com.project.models.Participant;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.ParticipantRepository;
import com.project.services.participant.ParticipantService;
import com.project.services.participant.ParticipantServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ParticipantMapperImpl.class, ParticipantServiceImpl.class})
public class ParticipantServiceIntegrationTest {

    public static final String CNP = "2980804776699";
    public static final long COMPETITION_ID = 1L;

    @MockBean
    private ParticipantRepository participantRepository;

    @MockBean
    private CompetitionRepository competitionRepository;

    @SpyBean
    private ParticipantMapper participantMapper;

    @Autowired
    private ParticipantService participantService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(participantRepository, competitionRepository, participantMapper);
    }

    @Test
    public void testGetParticipantByCnp() {
        Participant participant = getParticipantMock(CNP, "Ioana");

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.of(participant));

        ParticipantDto actualParticipant = participantService.getParticipantByCnp(CNP);

        verifyAssertions(participant.getName(), List.of(COMPETITION_ID), actualParticipant);

        verify(participantRepository).findByCnp(anyString());
        verify(participantMapper).participantEntityToDto(any());
        verify(participantMapper).extractCompetitions(any());
    }

    private void verifyAssertions(String name, List<Long> compIds, ParticipantDto actualParticipant) {
        assertEquals(name, actualParticipant.getName());
        assertEquals(compIds, actualParticipant.getCompetitionsIds());
    }

    private Participant getParticipantMock(String cnp, String name) {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Color Run");

        return Participant.builder()
                .cnp(cnp)
                .name(name)
                .competitions(Arrays.asList(competition))
                .build();
    }

    private Competition getCompetitionMock(Long id, String name) {
        return Competition.builder()
                .competitionId(id)
                .name(name)
                .ticketFee(30.0)
                .raisedMoney(30.0)
                .participants(new ArrayList<>())
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

        when(participantRepository.findAll()).thenReturn(participants);

        List<ParticipantDto> actualParticipants = participantService.getAllParticipants();

        assertEquals(2, actualParticipants.size());
        verifyAssertions(participant.getName(), List.of(COMPETITION_ID), actualParticipants.get(0));
        verifyAssertions(participant2.getName(), List.of(COMPETITION_ID), actualParticipants.get(1));

        verify(participantRepository).findAll();
        verify(participantMapper, times(2)).participantEntityToDto(any());
        verify(participantMapper, times(2)).extractCompetitions(any());
    }

    @Test
    public void testCreateNewParticipant() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        ParticipantDto participantDto = ParticipantDto.builder()
              .cnp(CNP)
              .competitionsIds(List.of(COMPETITION_ID))
              .build();

        Competition competition = participant.getCompetitions().get(0);

        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.ofNullable(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        participantService.createNewParticipant(participantDto);

        verify(participantRepository).save(any(Participant.class));
        verify(competitionRepository).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

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

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.of(participant));
        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.ofNullable(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(competitionRepository.findById(COMPETITION_ID + 1L)).thenReturn(Optional.ofNullable(competition2));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition2);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        participantService.updateParticipant(updatedParticipant);

        verify(participantRepository).findByCnp(anyString());
        verify(competitionRepository, times(2)).findById(anyLong());
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
    public void testUpdateParticipant_shouldThrowParticipantAlreadyEnrolledException() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        ParticipantDto updatedParticipant = ParticipantDto.builder()
                .cnp(CNP)
                .name("Ioana")
                .competitionsIds(Arrays.asList(COMPETITION_ID))
                .build();

        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.ofNullable(participant));

        RuntimeException exception = assertThrows(ParticipantAlreadyEnrolledException.class,
                () -> participantService.updateParticipant(updatedParticipant));
        assertTrue(exception.getMessage().contains("This participant is already enrolled to the competition(s) requested!"));

        verify(participantRepository).findByCnp(anyString());
    }

    @Test
    public void testDeleteParticipant() {
        Participant participant = getParticipantMock(CNP, "Ioana");
        participant.setCompetitions(new ArrayList<>());

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

    @Test
    public void testDeleteParticipant_shouldThrowBadRequestException() {
        Participant participant = getParticipantMock(CNP, "Jane");
        when(participantRepository.findByCnp(CNP)).thenReturn(Optional.ofNullable(participant));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> participantService.deleteParticipant(CNP));
        assertTrue(exception.getMessage().contains("The participant can not withdraw from a competition!"));

        verify(participantRepository).findByCnp(anyString());
    }

}
