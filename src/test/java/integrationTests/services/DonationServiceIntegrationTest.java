package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.dtos.DonationDto;
import com.project.exceptions.NotEnoughFundsException;
import com.project.mappers.DonationMapper;
import com.project.mappers.DonationMapperImpl;
import com.project.models.CharityPerson;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import com.project.services.donation.DonationService;
import com.project.services.donation.DonationServiceImpl;
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
@ContextConfiguration(classes = {DonationMapperImpl.class, DonationServiceImpl.class})
public class DonationServiceIntegrationTest {

    private static final long DONATION_ID = 100L;
    private static final long COMPETITION_ID = 300L;

    @MockBean
    private DonationRepository donationRepository;

    @MockBean
    private CompetitionRepository competitionRepository;

    @MockBean
    private CharityPersonRepository charityPersonRepository;

    @SpyBean
    private DonationMapper donationMapper;

    @Autowired
    private DonationService donationService;

    @Test
    public void testGetDonationById() {
        CharityPerson charityPerson = getCharityPersonMock("5180101253377", 1000.0, 0.0, 123L);

        Competition competition = getCompetitionMock(2000.0);

        Donation donation = Donation.builder()
                .donationId(DONATION_ID)
                .totalFunds(100.0)
                .charityPerson(charityPerson)
                .competition(competition)
                .build();

        when(donationRepository.findById(DONATION_ID)).thenReturn(Optional.of(donation));

        DonationDto actualDonation = donationService.getDonationById(DONATION_ID);

        assertEquals(COMPETITION_ID, actualDonation.getCompetitionId());
        assertEquals(donation.getTotalFunds(), actualDonation.getTotalFunds());
        assertEquals(donation.getCharityPerson().getPersonCnp(), actualDonation.getCharityPersonCnp());

        verify(donationRepository).findById(anyLong());
        verify(donationMapper).donationEntityToDto(any());
    }

    private Competition getCompetitionMock(Double budget) {
        return Competition.builder()
                .competitionId(COMPETITION_ID)
                .raisedMoney(budget)
                .name("Color run")
                .build();
    }

    private CharityPerson getCharityPersonMock(String cnp, Double neededFund, Double raisedFund, Long iban) {
        return CharityPerson.builder()
                .personCnp(cnp)
                .raisedFund(raisedFund)
                .neededFund(neededFund)
                .iban(iban)
                .build();
    }

    @Test
    public void testGetDonationById_shouldReturnNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(donationRepository).findById(DONATION_ID);

        assertThrows(ResponseStatusException.class, () -> donationService.getDonationById(DONATION_ID));

        verify(donationRepository).findById(anyLong());
    }

    @Test
    public void testCreateNewDonation() {
        CharityPerson charityPerson = getCharityPersonMock("5180101253377", 1000.0, 0.0, 123L);
        charityPerson.setDonations(new ArrayList<>());
        CharityPerson charityPerson2 = getCharityPersonMock("6180101253370", 1000.0, 100.0, 125L);

        Competition competition = getCompetitionMock(1500.0);
        Double donationFund = 0.02 * competition.getRaisedMoney();

        List<CharityPerson> charityPersons = Arrays.asList(charityPerson, charityPerson2);

        Donation donation = Donation.builder()
                .competition(competition)
                .totalFunds(donationFund)
                .charityPerson(charityPerson)
                .build();

        DonationDto donationDto = DonationDto.builder()
              .competitionId(competition.getCompetitionId())
              .totalFunds(donationFund)
              .charityPersonCnp(charityPerson.getPersonCnp())
              .build();

        when(charityPersonRepository.findAll()).thenReturn(charityPersons);
        when(competitionRepository.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);
        when(charityPersonRepository.findByPersonCnp("5180101253377")).thenReturn(Optional.ofNullable(charityPerson));
        when(charityPersonRepository.save(any(CharityPerson.class))).thenReturn(charityPerson);

        donationService.createNewDonation(donationDto);

        verify(charityPersonRepository).findAll();
        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).save(any(CharityPerson.class));
        verify(competitionRepository, times(2)).getById(anyLong());
        verify(competitionRepository).save(any(Competition.class));
        verify(donationRepository).save(any(Donation.class));
    }

    @Test
    public void testCreateNewDonation_shouldThrowNotEnoughFundsException() {
        CharityPerson charityPerson = getCharityPersonMock("5180101253377", 1000.0, 0.0, 123L);
        charityPerson.setDonations(new ArrayList<>());
        CharityPerson charityPerson2 = getCharityPersonMock("6180101253370", 1000.0, 100.0, 125L);

        Competition competition = getCompetitionMock(0.0);
        Double donationFund = 0.0;

        List<CharityPerson> charityPersons = Arrays.asList(charityPerson, charityPerson2);

        DonationDto donationDto = DonationDto.builder()
              .competitionId(competition.getCompetitionId())
              .totalFunds(competition.getRaisedMoney())
              .charityPersonCnp(charityPerson.getPersonCnp())
              .build();

        when(charityPersonRepository.findAll()).thenReturn(charityPersons);
        when(competitionRepository.getById(COMPETITION_ID)).thenReturn(competition);

        RuntimeException exception = assertThrows(NotEnoughFundsException.class,
                () -> donationService.createNewDonation(donationDto));
        assertTrue(exception.getMessage().contains("All donation funds are consumed! Donation not possible!"));

        verify(charityPersonRepository).findAll();
        verify(competitionRepository).getById(anyLong());
    }

    @Test
    public void testCreateNewDonation_shouldReturnNotFoundExceptionWhenNoPersonsFound() {
        DonationDto donationDto = new DonationDto();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(charityPersonRepository).findAll();

        assertThrows(ResponseStatusException.class, () -> donationService.createNewDonation(donationDto));

        verify(charityPersonRepository).findAll();
    }

    @Test
    public void testCreateNewDonation_shouldReturnNotFoundExceptionWhenNoCompetitionFound() {
        DonationDto donationDto = new DonationDto();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(competitionRepository).getById(COMPETITION_ID);

        assertThrows(ResponseStatusException.class, () -> donationService.createNewDonation(donationDto));

        verify(charityPersonRepository).findAll();
    }

}
