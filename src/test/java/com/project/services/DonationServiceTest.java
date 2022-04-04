package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.DonationDto;
import com.project.exceptions.NotEnoughFundsException;
import com.project.models.CharityPerson;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import com.project.services.donation.DonationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DataJpaTest
public class DonationServiceTest {

    private static final long DONATION_ID = 100L;
    private static final long COMPETITION_ID = 300L;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CharityPersonRepository charityPersonRepository;

    @InjectMocks
    private DonationServiceImpl donationService;

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

        DonationDto expectedDonation = ModelToDto.donationToDto(donation);

        when(donationRepository.findById(DONATION_ID)).thenReturn(Optional.of(donation));

        DonationDto actualDonation = donationService.getDonationById(DONATION_ID);

        assertEquals(expectedDonation.getCompetitionId(), actualDonation.getCompetitionId());
        assertEquals(expectedDonation.getTotalFunds(), actualDonation.getTotalFunds());
        assertEquals(expectedDonation.getCharityPersonCnp(), actualDonation.getCharityPersonCnp());

        verify(donationRepository).findById(anyLong());
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

        DonationDto donationDto = ModelToDto.donationToDto(donation);

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

        Donation donation = Donation.builder()
                .competition(competition)
                .totalFunds(donationFund)
                .charityPerson(charityPerson)
                .build();

        DonationDto donationDto = ModelToDto.donationToDto(donation);

        when(charityPersonRepository.findAll()).thenReturn(charityPersons);
        when(competitionRepository.getById(COMPETITION_ID)).thenReturn(competition);

        assertThrows(NotEnoughFundsException.class, () -> donationService.createNewDonation(donationDto));

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
