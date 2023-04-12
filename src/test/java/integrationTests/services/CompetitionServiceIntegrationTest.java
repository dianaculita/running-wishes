package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.BadRequestException;

import com.project.dtos.CompetitionDto;
import com.project.dtos.SportDto;
import com.project.mappers.CompetitionMapper;
import com.project.mappers.CompetitionMapperImpl;
import com.project.mappers.SportMapper;
import com.project.mappers.SportMapperImpl;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.models.Sport;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import com.project.services.competition.CompetitionService;
import com.project.services.competition.CompetitionServiceImpl;
import com.project.services.sport.SportService;
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
@ContextConfiguration(classes = {CompetitionServiceImpl.class, CompetitionMapperImpl.class, SportMapperImpl.class})
public class CompetitionServiceIntegrationTest {

    public static final long COMPETITION_ID = 100L;
    public static final long SPORT_ID = 100L;

    @MockBean
    private CompetitionRepository competitionRepository;

    @MockBean
    private SportService sportService;

    @MockBean
    private DonationRepository donationRepository;

    @SpyBean
    private CompetitionMapper competitionMapper;

    @SpyBean
    private SportMapper sportMapper;

    @Autowired
    private CompetitionService competitionService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(competitionRepository, sportService, competitionMapper, sportMapper);
    }

    @Test
    public void testGetCompetitionById() {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Run Fest");

        when(competitionRepository.findById(COMPETITION_ID)).thenReturn(Optional.of(competition));

        CompetitionDto actualCompetition = competitionService.getCompetitionById(COMPETITION_ID);

        verifyAssertions(competition.getLocation(), competition.getName(), competition.getTicketFee(), actualCompetition);

        verify(competitionRepository).findById(anyLong());
        verify(competitionMapper).competitionEntityToDto(any());
        verify(competitionMapper).extractParticipants(any());
        verify(competitionMapper).extractSponsors(any());
    }

    private void verifyAssertions(String location, String name, Double ticketFee, CompetitionDto actualCompetition) {
        assertEquals(location, actualCompetition.getLocation());
        assertEquals(name, actualCompetition.getName());
        assertEquals(ticketFee, actualCompetition.getTicketFee());
    }

    private Competition getCompetitionMock(Long id, String name) {
        Sport sport = Sport.builder().sportId(SPORT_ID).name("running").build();
        Competition competition = Competition.builder()
                                             .competitionId(id)
                                             .name(name)
                                             .ticketFee(100.0)
                                             .startDate(LocalDate.of(2022, 3, 6))
                                             .location("Bucharest")
                                             .sport(sport)
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

        when(competitionRepository.findAll()).thenReturn(competitions);

        List<CompetitionDto> actualCompetitions = competitionService.getAllCompetitions();

        assertEquals(2, actualCompetitions.size());
        verifyAssertions(competition.getLocation(), competition.getName(), competition.getTicketFee(),
              actualCompetitions.get(0));
        verifyAssertions(competition2.getLocation(), competition2.getName(), competition2.getTicketFee(),
              actualCompetitions.get(1));

        verify(competitionRepository).findAll();
        verify(competitionMapper, times(2)).competitionEntityToDto(any());
        verify(competitionMapper, times(2)).extractParticipants(any());
        verify(competitionMapper, times(2)).extractSponsors(any());
    }

    @Test
    public void testCreateNewCompetition() {
        Competition competition = getCompetitionMock(null, "Run Fest");
        CompetitionDto competitionDto = CompetitionDto.builder()
                                                      .sportId(competition.getSport().getSportId())
                                                      .startDate(competition.getStartDate())
                                                      .build();
        SportDto sportDto = SportDto.builder()
                                    .sportId(competition.getSport().getSportId())
                                    .name(competition.getSport().getName())
                                    .build();

        when(sportService.getSportById(SPORT_ID)).thenReturn(sportDto);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        competitionService.createNewCompetition(competitionDto);

        verify(sportService).getSportById(anyLong());
        verify(sportMapper).sportDtoToEntity(any());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    public void testUpdateCompetition() {
        Competition competition = getCompetitionMock(COMPETITION_ID, "Run Fest");
        CompetitionDto competitionDto = CompetitionDto.builder()
                                                      .competitionId(COMPETITION_ID)
                                                      .name(competition.getName())
                                                      .location(competition.getLocation())
                                                      .startDate(competition.getStartDate())
                                                      .build();

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
