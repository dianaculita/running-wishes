package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.CompetitionDto;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.models.Sport;
import com.project.repositories.CompetitionRepository;
import com.project.services.competition.CompetitionServiceImpl;
import com.project.services.sport.SportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class CompetitionServiceTest {

    public static final long COMPETITION_ID = 100L;
    public static final long SPORT_ID = 100L;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private SportService sportService;

    @InjectMocks
    private CompetitionServiceImpl competitionService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(competitionRepository, sportService);
    }

    @Test
    public void testGetCompetitionById() {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Run Fest");

        CompetitionDto expectedCompetition = ModelToDto.competitionToDto(competition);

        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.of(competition));

        CompetitionDto actualCompetition = competitionService.getCompetitionById(COMPETITION_ID);

        verifyAssertions(expectedCompetition, actualCompetition);

        verify(competitionRepository).findById(anyLong());
    }

    private void verifyAssertions(CompetitionDto expectedCompetition, CompetitionDto actualCompetition) {
        assertEquals(expectedCompetition.getLocation(), actualCompetition.getLocation());
        assertEquals(expectedCompetition.getName(), actualCompetition.getName());
        assertEquals(expectedCompetition.getTicketFee(), actualCompetition.getTicketFee());
    }

    private Competition getCompetitionMock(Long id, String name) {
        Sport sport = Sport.builder().sportId(SPORT_ID).name("running").build();
        Competition competition = Competition.builder()
                .competitionId(id)
                .name(name)
                .ticketFee(100.0)
                .location("Bucharest")
                .sport(sport)
                .participants(new ArrayList<>())
                .sponsors(new ArrayList<>())
                .build();

        return competition;
    }

    @Test
    public void testGetCompetitionById_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(competitionRepository).findById(COMPETITION_ID);

        assertThrows(ResponseStatusException.class, () -> competitionService.getCompetitionById(COMPETITION_ID));

        verify(competitionRepository).findById(anyLong());
    }

    @Test
    public void testGetAllCompetitions() {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Run Fest");
        Competition competition2 = getCompetitionMock(COMPETITION_ID + 1L, "Run for children");
        List<Competition> competitions = Arrays.asList(competition, competition2);

        CompetitionDto competitionDto = ModelToDto.competitionToDto(competition);
        CompetitionDto competitionDto2 = ModelToDto.competitionToDto(competition2);
        List<CompetitionDto> expectedCompetitions = Arrays.asList(competitionDto, competitionDto2);

        when(competitionRepository.findAll()).thenReturn(competitions);

        List<CompetitionDto> actualCompetitions = competitionService.getAllCompetitions();

        assertEquals(expectedCompetitions.size(), actualCompetitions.size());
        verifyAssertions(expectedCompetitions.get(0), actualCompetitions.get(0));
        verifyAssertions(expectedCompetitions.get(1), actualCompetitions.get(1));

        verify(competitionRepository).findAll();
    }

    @Test
    public void testCreateNewCompetition() {
        Competition competition = getCompetitionMock(null, "Run Fest");
        CompetitionDto competitionDto = ModelToDto.competitionToDto(competition);

        when(sportService.getSportById(SPORT_ID)).thenReturn(ModelToDto.sportToDto(competition.getSport()));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        competitionService.createNewCompetition(competitionDto);

        verify(sportService).getSportById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    public void testUpdateCompetition() {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Run Fest");
        CompetitionDto competitionDto = ModelToDto.competitionToDto(competition);

        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        competitionService.updateCompetition(competitionDto);

        verify(competitionRepository).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    public void testUpdateCompetition_shouldThrowNotFoundException() {
        CompetitionDto competitionDto = new CompetitionDto();
        competitionDto.setCompetitionId(COMPETITION_ID);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(competitionRepository).findById(COMPETITION_ID);

        assertThrows(ResponseStatusException.class, () -> competitionService.updateCompetition(competitionDto));

        verify(competitionRepository).findById(anyLong());
    }

    @Test
    public void testDeleteCompetition() {
        Competition competition = new Competition();
        competition.setSponsors(new ArrayList<>());
        competition.setDonations(new ArrayList<>());
        competition.setParticipants(new ArrayList<>());

        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        doNothing().when(competitionRepository).delete(competition);

        competitionService.deleteCompetition(COMPETITION_ID);

        verify(competitionRepository).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
        verify(competitionRepository).delete(any(Competition.class));
    }

    @Test
    public void testDeleteCompetition_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(competitionRepository).findById(COMPETITION_ID);

        assertThrows(ResponseStatusException.class, () -> competitionService.deleteCompetition(COMPETITION_ID));

        verify(competitionRepository).findById(anyLong());
    }

    @Test
    public void testDeleteCompetition_shouldThrowBadRequestException() {
        Competition competition = new Competition();
        competition.setSponsors(new ArrayList<>());
        competition.setParticipants(new ArrayList<>());
        Donation donation = new Donation();
        competition.setDonations(List.of(donation));

        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> competitionService.deleteCompetition(COMPETITION_ID));
        assertTrue(exception.getMessage().contains("Competition can not be deleted as donations are in progress!"));

        verify(competitionRepository).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

}
