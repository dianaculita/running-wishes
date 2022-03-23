package com.project.services.sponsor;

import com.project.converters.ModelToDto;
import com.project.dtos.SponsorDto;
import com.project.models.Competition;
import com.project.models.Donation;
import com.project.models.Sponsor;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.DonationRepository;
import com.project.repositories.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SponsorServiceImpl implements SponsorService {

    private final SponsorRepository sponsorRepository;

    private final CompetitionRepository competitionRepository;

    private final DonationRepository donationRepository;

    @Autowired
    public SponsorServiceImpl(SponsorRepository sponsorRepository,
                              CompetitionRepository competitionRepository,
                              DonationRepository donationRepository) {
        this.sponsorRepository = sponsorRepository;
        this.competitionRepository = competitionRepository;
        this.donationRepository = donationRepository;
    }

    @Override
    public SponsorDto getSponsorById(Long id) {
        return ModelToDto.sponsorToDto(getById(id));
    }

    private Sponsor getById(Long id) {
        return sponsorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<SponsorDto> getAllSponsors() {
        return sponsorRepository.findAll().stream()
                .map(ModelToDto::sponsorToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long createNewSponsor(SponsorDto sponsorDto) {
        Sponsor sponsor = Sponsor.builder()
                .name(sponsorDto.getName())
                .competitions(sponsors(sponsorDto.getCompetitionsIds()))
                .build();

        return sponsorRepository.save(sponsor).getSponsorId();
    }

    private List<Competition> sponsors(List<Long> ids) {
        return competitionRepository.findByCompetitionIdIn(ids);
    }

    @Override
    public void updateSponsor(SponsorDto sponsorDto) {
        Sponsor sponsor = getById(sponsorDto.getSponsorId());

        sponsor.setName(sponsorDto.getName());
        sponsor.setDonations(donatesTo(sponsorDto.getDonationsIds()));
        sponsor.setCompetitions(sponsors(sponsorDto.getCompetitionsIds()));

        sponsorRepository.save(sponsor);
    }

    private List<Donation> donatesTo(List<Long> ids) {
        return donationRepository.findByDonationIdIn(ids);
    }

    @Override
    public void deleteSponsor(Long id) {
        sponsorRepository.delete(getById(id));
    }

}
