package com.project.services.donation;

import com.project.dtos.DonationDto;
import com.project.exceptions.NotEnoughFundsException;
import com.project.mappers.DonationMapper;
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

    private final DonationMapper donationMapper;

    @Autowired
    public DonationServiceImpl(DonationRepository donationRepository,
                               CompetitionRepository competitionRepository,
                               CharityPersonRepository charityPersonRepository,
                               DonationMapper donationMapper) {
        this.donationRepository = donationRepository;
        this.competitionRepository = competitionRepository;
        this.charityPersonRepository = charityPersonRepository;
        this.donationMapper = donationMapper;
    }

    @Override
    public DonationDto getDonationById(Long id) {
        return donationMapper.donationEntityToDto(getById(id));
    }

    private Donation getById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<DonationDto> getAllDonations() {
        return donationRepository.findAll().stream()
                .map(donationMapper::donationEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * The donation creation establishes a connection to a certain competition
     * It will be established to whom the donation is redirected and how much money the person
     * receives, based on the amount of money they have already received compared to how
     * much money they need in total
     * An update of the raisedFund for the requested person is also needed
     * The competition that has redirected the donation will have its fundraising budget updated
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
        donation = donationRepository.save(donation);
        updateCharityPersonRaisedFund(needingPerson.getPersonCnp(), totalFunds, donation);

        return donation.getDonationId();
    }

    private Competition getCompetition(DonationDto donationDto) {
        return competitionRepository.getById(donationDto.getCompetitionId());
    }

    /**
     * Searches for the most needing charity case
     */
    private CharityPerson getTheMostNeedingPerson() {
        return charityPersonRepository.findAll().stream()
                .max(Comparator.comparingDouble(pers -> pers.getNeededFund() - pers.getRaisedFund()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Calculates the amount of money that will be donated, based on the actual raised amount of money,
     * that was already updated with the paid tickets and sponsoring budget
     * The donation will be extracted from the actual competition donation funds
     * If donation is possible, 2% of the raised money will be donated; if not, the remained money
     * will be donated
     */
    private Double calculateTotalFunds(Competition competition) {
        Double donationAmount = 0.02 * competition.getRaisedMoney();

        if (competition.getRaisedMoney() > donationAmount) {
            updateCompetitionFundraisingBudget(competition.getCompetitionId(),
                    (-1) * donationAmount);
            return donationAmount;

        } else if (competition.getRaisedMoney() != 0.0) {
            updateCompetitionFundraisingBudget(competition.getCompetitionId(), 0.0);
            return competition.getRaisedMoney();
        } else {
            throw new NotEnoughFundsException();
        }

    }

    private void updateCompetitionFundraisingBudget(Long competitionId, Double sponsoringFunds) {
        Competition competition = competitionRepository.getById(competitionId);

        Double existingFunds = competition.getRaisedMoney();
        competition.setRaisedMoney(existingFunds + sponsoringFunds);

        competitionRepository.save(competition);
    }

    private void updateCharityPersonRaisedFund(String cnp, Double raisedFund, Donation donation) {
        CharityPerson charityPerson = charityPersonRepository.findByPersonCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Double existingFund = charityPerson.getRaisedFund();
        charityPerson.setRaisedFund(raisedFund + existingFund);
        charityPerson.getDonations().add(donation);

        charityPersonRepository.save(charityPerson);
    }

}
