package com.project.services.donation;

import com.project.converters.ModelToDto;
import com.project.dtos.DonationDto;
import com.project.models.CharityPerson;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;

    private final CompetitionRepository competitionRepository;

    private final CharityPersonRepository charityPersonRepository;

    @Autowired
    public DonationServiceImpl(DonationRepository donationRepository,
                               CompetitionRepository competitionRepository,
                               CharityPersonRepository charityPersonRepository) {
        this.donationRepository = donationRepository;
        this.competitionRepository = competitionRepository;
        this.charityPersonRepository = charityPersonRepository;
    }

    @Override
    public DonationDto getDonationById(Long id) {
        return ModelToDto.donationToDto(getById(id));
    }

    private Donation getById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<DonationDto> getAllDonations() {
        return donationRepository.findAll().stream()
                .map(ModelToDto::donationToDto)
                .collect(Collectors.toList());
    }

    /*
        the donation creation establishes a connection to a certain sponsor;
        it will be established to whom the donation is redirected and how much money the person receives,
        based on the amount of money they have already received compared to how much money they need in total
        an update of the raisedFund for the requested person is also needed
    */
    @Override
    public Long createNewDonation(DonationDto donationDto) {
        CharityPerson needingPerson = getTheMostNeedingPerson();
        Competition competition = getCompetition(donationDto);
        Double totalFunds = calculateTotalFunds(competition);

        Donation donation = Donation.builder()
                .competition(competition)
                .charityPerson(needingPerson)
                .totalFunds(totalFunds)
                .build();

        updateCharityPersonRaisedFund(needingPerson.getPersonCnp(), totalFunds);

        return donationRepository.save(donation).getDonationId();
    }

    private Competition getCompetition(DonationDto donationDto) {
        return competitionRepository.getById(donationDto.getCompetitionId());
    }

    /*
        searches for the most needing charity case, based on the amount of money they have raised compared
        to the amount of money they need
    */
    private CharityPerson getTheMostNeedingPerson() {
        return charityPersonRepository.findAll().stream()
                .max(Comparator.comparingDouble(pers -> pers.getNeededFund() - pers.getRaisedFund()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /*
        calculates the amount of money that will be donated, based on the actual raised amount of money,
        that was already updated with the paid tickets and sponsoring budget
        the donation will be extracted from the actual competition donation funds
     */
    private Double calculateTotalFunds(Competition competition) {
        Double donationAmount = 0.02 * competition.getRaisedMoney();

        if (competition.getRaisedMoney() > donationAmount) {
            updateCompetitionFundraisingBudget(competition.getCompetitionId(),
                    (-1) * donationAmount);
            return donationAmount;

        } else {
            updateCompetitionFundraisingBudget(competition.getCompetitionId(), 0.0);
            return competition.getRaisedMoney();
        }

    }

    private void updateCompetitionFundraisingBudget(Long competitionId, Double sponsoringFunds) {
        Competition competition = competitionRepository.getById(competitionId);

        Double existingFunds = competition.getRaisedMoney();
        competition.setRaisedMoney(existingFunds + sponsoringFunds);

        competitionRepository.save(competition);
    }

    private void updateCharityPersonRaisedFund(String cnp, Double raisedFund) {
        CharityPerson charityPerson = charityPersonRepository.findByPersonCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Double existingFund = charityPerson.getRaisedFund();
        charityPerson.setRaisedFund(raisedFund + existingFund);

        charityPersonRepository.save(charityPerson);
    }

    /*
        the update is no longer needed, because when a donation is created, the funds are redirected
        to the person in need
        an update is not justified. to send money to the same person, another donation will be necessary
     */
    @Override
    public void updateDonation(DonationDto donationDto) {}

    @Override
    public void deleteDonation(Long id) {
        donationRepository.delete(getById(id));
    }

}
