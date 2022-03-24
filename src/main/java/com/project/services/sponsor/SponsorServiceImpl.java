package com.project.services.sponsor;

import com.project.converters.ModelToDto;
import com.project.dtos.SponsorDto;
import com.project.models.Competition;
import com.project.models.Sponsor;
import com.project.repositories.CompetitionRepository;
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

    @Autowired
    public SponsorServiceImpl(SponsorRepository sponsorRepository,
                              CompetitionRepository competitionRepository) {
        this.sponsorRepository = sponsorRepository;
        this.competitionRepository = competitionRepository;
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

    /*
        when the sponsor is created, it will be associated with a competition, meaning that it will
        sponsor that competition (or each competition if more) with a fixed amount of money - sponsoringFunds
        as soon as the collaboration between a sponsor and a competition is established, the competition
        will benefit of the sponsoring funds and will save them for further donations, meaning that
        the competition fundraising budget will be updated
     */
    @Override
    public Long createNewSponsor(SponsorDto sponsorDto) {
        List<Long> competitionsIds = sponsorDto.getCompetitionsIds();

        Sponsor sponsor = Sponsor.builder()
                .name(sponsorDto.getName())
                .competitions(sponsors(competitionsIds))
                .sponsoringFunds(sponsorDto.getSponsoringFunds())
                .build();

        updateCompetitionsFundraisingBudget(competitionsIds, sponsorDto.getSponsoringFunds());

        return sponsorRepository.save(sponsor).getSponsorId();
    }

    private List<Competition> sponsors(List<Long> ids) {
        return competitionRepository.findByCompetitionIdIn(ids);
    }

    private void updateCompetitionsFundraisingBudget(List<Long> competitions, Double sponsoringFunds) {
        for (Long competitionId : competitions) {
            Competition competition = competitionRepository.getById(competitionId);

            Double existingFunds = competition.getRaisedMoney();
            competition.setRaisedMoney(existingFunds + sponsoringFunds);

            competitionRepository.save(competition);
        }
    }

    /*
        if updating a sponsor means changing its fundraising budget, then all the associated competitions
        need to update their existing funds -> the old sponsor fundraising budget will be extracted
        from the existing funds and the new budget will be added
     */
    @Override
    public void updateSponsor(SponsorDto sponsorDto) {
        Sponsor sponsor = getById(sponsorDto.getSponsorId());

        List<Long> competitionsIds = sponsor.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        updateCompetitionsFundraisingBudget(competitionsIds, (-1) * sponsor.getSponsoringFunds());

        sponsor.setName(sponsorDto.getName());
        sponsor.setSponsoringFunds(sponsorDto.getSponsoringFunds());
        sponsor.setCompetitions(sponsors(sponsorDto.getCompetitionsIds()));

        updateCompetitionsFundraisingBudget(sponsorDto.getCompetitionsIds(), sponsorDto.getSponsoringFunds());

        sponsorRepository.save(sponsor);
    }

    @Override
    public void deleteSponsor(Long id) {
        sponsorRepository.delete(getById(id));
    }

}
