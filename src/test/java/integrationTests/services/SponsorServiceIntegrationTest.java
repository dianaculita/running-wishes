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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.BadRequestException;

import com.project.dtos.SponsorDto;
import com.project.exceptions.SponsorAlreadyExistsException;
import com.project.mappers.SponsorMapper;
import com.project.mappers.SponsorMapperImpl;
import com.project.models.Competition;
import com.project.models.Sponsor;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.SponsorRepository;
import com.project.services.sponsor.SponsorServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
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
@ContextConfiguration(classes = {SponsorMapperImpl.class, SponsorServiceImpl.class})
public class SponsorServiceIntegrationTest {

    public static final long SPONSOR_ID = 100L;

    @MockBean
    private SponsorRepository sponsorRepository;

    @MockBean
    private CompetitionRepository competitionRepository;

    @SpyBean
    private SponsorMapper sponsorMapper;

    @Autowired
    private SponsorServiceImpl sponsorService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(sponsorRepository, competitionRepository, sponsorMapper);
    }

    @Test
    public void testGetSponsorById() {
        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.ofNullable(sponsor));

        SponsorDto actualSponsor = sponsorService.getSponsorById(SPONSOR_ID);

        verifyAssertions(sponsor.getSponsoringFunds(), sponsor.getName(), actualSponsor);

        verify(sponsorRepository).findById(anyLong());
        verify(sponsorMapper).sponsorEntityToDto(any());
    }

    private void verifyAssertions(Double funds, String name, SponsorDto actualSponsor) {
        assertEquals(funds, actualSponsor.getSponsoringFunds());
        assertEquals(name, actualSponsor.getName());
    }

    private Sponsor getSponsorMock(String name, Long id) {
        return Sponsor.builder()
                .sponsorId(id)
                .name(name)
                .sponsoringFunds(100.0)
                .competitions(new ArrayList<>())
                .build();
    }

    @Test
    public void testGetSponsorById_shouldReturnNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(sponsorRepository).findById(SPONSOR_ID);

        assertThrows(ResponseStatusException.class, () -> sponsorService.getSponsorById(SPONSOR_ID));

        verify(sponsorRepository).findById(anyLong());
    }

    @Test
    public void testGetAllSponsors() {
        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        Sponsor sponsor2 = getSponsorMock("Pro", SPONSOR_ID + 1L);
        List<Sponsor> sponsorList = Arrays.asList(sponsor, sponsor2);

        when(sponsorRepository.findAll()).thenReturn(sponsorList);

        List<SponsorDto> actualSponsors = sponsorService.getAllSponsors();

        assertEquals(2, actualSponsors.size());
        verifyAssertions(sponsor.getSponsoringFunds(), sponsor.getName(), actualSponsors.get(0));
        verifyAssertions(sponsor2.getSponsoringFunds(),sponsor2.getName(), actualSponsors.get(1));

        verify(sponsorRepository).findAll();
        verify(sponsorMapper, times(2)).sponsorEntityToDto(any());
    }

    @Test
    public void testCreateNewSponsorWithoutCompetitions() {
        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(new ArrayList<>());
        SponsorDto sponsorDto = SponsorDto.builder()
              .name(sponsor.getName())
              .sponsorId(SPONSOR_ID)
              .competitionsIds(List.of())
              .build();

        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);

        sponsorService.createNewSponsor(sponsorDto);

        verify(sponsorRepository).save(any(Sponsor.class));
    }

    @Test
    public void testCreateNewSponsorWithCompetitions() {
        Long competitionId = 100L;
        Competition competition = getCompetitionMock(competitionId, "Color run");

        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(List.of(competition));
        SponsorDto sponsorDto = SponsorDto.builder()
              .name(sponsor.getName())
              .sponsoringFunds(sponsor.getSponsoringFunds())
              .competitionsIds(List.of(competitionId))
              .build();

        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        sponsorService.createNewSponsor(sponsorDto);

        verify(sponsorRepository).save(any(Sponsor.class));
        verify(competitionRepository).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

    private Competition getCompetitionMock(Long id, String name) {
        return Competition.builder()
                .competitionId(id)
                .name(name)
                .raisedMoney(0.0)
                .sponsors(new ArrayList<>())
                .build();
    }

    @Test
    public void testCreateNewSponsorWithCompetitions_shouldThrowNotFoundException() {
        Long competitionId = 100L;
        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        SponsorDto sponsorDto = SponsorDto.builder().build();
        sponsorDto.setCompetitionsIds(List.of(competitionId));

        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(competitionRepository).findById(competitionId);

        assertThrows(ResponseStatusException.class, () -> sponsorService.createNewSponsor(sponsorDto));

        verify(sponsorRepository).save(any(Sponsor.class));
        verify(competitionRepository).findById(anyLong());
    }

    @Test
    public void testUpdateSponsor() {
        Long competitionId = 100L;
        Long competitionId2 = 101L;
        Competition competition = getCompetitionMock(competitionId, "Color run");
        Competition competition2 = getCompetitionMock(competitionId2, "Run");

        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(List.of(competition));
        competition.setSponsors(List.of(sponsor));

        SponsorDto sponsorDto = SponsorDto.builder()
              .sponsorId(SPONSOR_ID)
              .name(sponsor.getName())
              .competitionsIds(List.of(competitionId))
              .sponsoringFunds(sponsor.getSponsoringFunds())
              .build();
        sponsorDto.setCompetitionsIds(Arrays.asList(competitionId, competitionId2));

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionRepository.findById(competitionId2)).thenReturn(Optional.of(competition2));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition2);
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);

        sponsorService.updateSponsor(sponsorDto);

        verify(sponsorRepository).findById(anyLong());
        verify(sponsorRepository, times(2)).save(any(Sponsor.class));
        verify(competitionRepository, times(2)).findById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Disabled
    @Test
    public void testUpdateSponsor_whenCompetitionsRemoved() {
        Long competitionId = 100L;
        Long competitionId2 = 101L;
        Competition competition = getCompetitionMock(competitionId, "Color run");
        Competition competition2 = getCompetitionMock(competitionId2, "Run");

        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(Arrays.asList(competition, competition2));

        competition.setSponsors(List.of(sponsor));
        competition2.setSponsors(List.of(sponsor));

        SponsorDto sponsorDto = SponsorDto.builder().build();
        sponsorDto.setCompetitionsIds(List.of(competitionId2));

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(competitionRepository.getById(competitionId)).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);

        sponsorService.updateSponsor(sponsorDto);

        verify(sponsorRepository).findById(anyLong());
        verify(sponsorRepository, times(2)).save(any(Sponsor.class));
        verify(competitionRepository).getById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    public void testUpdateSponsor_whenCompetitionsNotChanged() {
        Long competitionId = 100L;
        Competition competition = getCompetitionMock(competitionId, "Color run");

        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(List.of(competition));

        SponsorDto sponsorDto = SponsorDto.builder()
              .name(sponsor.getName())
              .sponsorId(SPONSOR_ID)
              .competitionsIds(List.of(competitionId))
              .build();

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);

        RuntimeException exception = assertThrows(SponsorAlreadyExistsException.class,
                () -> sponsorService.updateSponsor(sponsorDto));
        assertTrue(exception.getMessage().contains("The sponsor already has a contract with the competition(s) requested!"));

        verify(sponsorRepository).findById(anyLong());
        verify(sponsorRepository).save(any(Sponsor.class));
    }

    @Test
    public void testUpdateSponsor_whenFundIsModified() {
        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);

        SponsorDto sponsorDto = SponsorDto.builder()
              .name(sponsor.getName())
              .sponsorId(SPONSOR_ID)
              .sponsoringFunds(300.0)
              .competitionsIds(List.of())
              .build();

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> sponsorService.updateSponsor(sponsorDto));
        assertTrue(exception.getMessage().contains("Sponsoring funds can not be changed!"));

        verify(sponsorRepository).findById(anyLong());
        verify(sponsorRepository).save(any(Sponsor.class));
    }

    @Test
    public void testDeleteSponsor() {
        Long competitionId = 100L;
        Competition competition = getCompetitionMock(competitionId, "Color run");

        Sponsor sponsor = getSponsorMock("Pro tv", SPONSOR_ID);
        sponsor.setCompetitions(List.of(competition));

        when(sponsorRepository.findById(SPONSOR_ID)).thenReturn(Optional.of(sponsor));
        when(competitionRepository.getById(competitionId)).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        doNothing().when(sponsorRepository).delete(sponsor);

        sponsorService.deleteSponsor(SPONSOR_ID);

        verify(sponsorRepository).findById(anyLong());
        verify(competitionRepository).getById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
        verify(sponsorRepository).delete(any(Sponsor.class));
    }


}
