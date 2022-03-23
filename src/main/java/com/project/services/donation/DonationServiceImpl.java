package com.project.services.donation;

import com.project.converters.DtoToModel;
import com.project.converters.ModelToDto;
import com.project.dtos.DonationDto;
import com.project.models.CharityPerson;
import com.project.models.Donation;
import com.project.models.Sponsor;
import com.project.repositories.DonationRepository;
import com.project.services.charity.CharityPersonService;
import com.project.services.sponsor.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;

    private final SponsorService sponsorService;

    private final CharityPersonService charityPersonService;

    @Autowired
    public DonationServiceImpl(DonationRepository donationRepository,
                               SponsorService sponsorService,
                               CharityPersonService charityPersonService) {
        this.donationRepository = donationRepository;
        this.sponsorService = sponsorService;
        this.charityPersonService = charityPersonService;
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

    @Override
    public Long createNewDonation(DonationDto donationDto) {
        Donation donation = Donation.builder()
                .donationId(donationDto.getDonationId())
                .sponsor(getSponsor(donationDto))
                .charityPerson(getCharityPerson(donationDto))
                .build();

        return donationRepository.save(donation).getDonationId();
    }

    private CharityPerson getCharityPerson(DonationDto donationDto) {
        return DtoToModel.fromCharityPersonDto(charityPersonService.getCharityPersonByCnp(donationDto.getCharityPersonCnp()));
    }

    private Sponsor getSponsor(DonationDto donationDto) {
        return DtoToModel.fromSponsorDto(sponsorService.getSponsorById(donationDto.getSponsorId()));
    }

    @Override
    public void updateDonation(DonationDto donationDto) {
        Donation donation = donationRepository.getById(donationDto.getDonationId());

        donation.setCharityPerson(getCharityPerson(donationDto));
        donation.setSponsor(getSponsor(donationDto));
        donation.setTotalFunds(donationDto.getTotalFunds());

        donationRepository.save(donation);
    }

    @Override
    public void deleteDonation(Long id) {
        donationRepository.delete(getById(id));
    }

}
