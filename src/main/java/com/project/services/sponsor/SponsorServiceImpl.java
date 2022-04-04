package com.project.services.sponsor;

import com.project.converters.ModelToDto;
import com.project.dtos.SponsorDto;
import com.project.exceptions.SponsorAlreadyExistsException;
import com.project.models.Competition;
import com.project.models.Sponsor;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
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

    /**
     * When the sponsor is created, it will be associated with a competition; it will
     * sponsor that competition (or each competition if more) with a fixed amount of money
     * As soon as the collaboration between a sponsor and a competition is established,
     * the competition will benefit of the sponsoring funds and will save them for further
     * donations; the competition fundraising budget will be updated
     */
    @Override
    public Long createNewSponsor(SponsorDto sponsorDto) {
        Sponsor sponsor = Sponsor.builder()
                .name(sponsorDto.getName())
                .sponsoringFunds(sponsorDto.getSponsoringFunds())
                .build();

        sponsor = sponsorRepository.save(sponsor);
        updateCompetitionsFundraisingBudget(sponsor, sponsorDto.getCompetitionsIds(), sponsorDto.getSponsoringFunds());

        return sponsor.getSponsorId();
    }

    private void updateCompetitionsFundraisingBudget(Sponsor sponsor, List<Long> competitionsIds, Double sponsoringFunds) {
        final Sponsor sponsorFinal = sponsor;

        competitionsIds.forEach(id -> {
            Competition competition = competitionRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // if a collaboration between sponsor and competition isn't already established
            if (!competition.getSponsors().contains(sponsor)) {
                competition.getSponsors().add(sponsorFinal);
                competition.setRaisedMoney(competition.getRaisedMoney() + sponsoringFunds);
                competitionRepository.save(competition);
            }
        });
    }

    /**
     * The sponsoring fund is fixed and can not be changed by update
     * The sponsor can withdraw from a collaboration with a competition,
     * or can add more collaborations; update on each competition is needed
     */
    @Override
    public void updateSponsor(SponsorDto sponsorDto) {
        Sponsor sponsor = getById(sponsorDto.getSponsorId());

        List<Long> oldCompetitionsIds = sponsor.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        sponsor.setName(sponsorDto.getName());
        sponsorRepository.save(sponsor);

        List<Long> updatedCompetitionsIds = sponsorDto.getCompetitionsIds();

        // unnecessary update
        if (updatedCompetitionsIds.equals(oldCompetitionsIds) && oldCompetitionsIds.size() > 0) {
            throw new SponsorAlreadyExistsException();
        }

        if (!sponsor.getSponsoringFunds().equals(sponsorDto.getSponsoringFunds())) {
            throw new BadRequestException("Sponsoring funds can not be changed!");
        }

        // if any collaboration is suspended, it needs to be removed from the competition
        // and the competition fundraising budget needs to be updated
        if (updatedCompetitionsIds.size() < oldCompetitionsIds.size()) {
            oldCompetitionsIds.removeAll(updatedCompetitionsIds);
            oldCompetitionsIds.forEach(competitionId -> {
                removeSponsoringFunds(sponsor, competitionId);
            });
        }

        updateCompetitionsFundraisingBudget(sponsor, updatedCompetitionsIds, sponsorDto.getSponsoringFunds());
        sponsorRepository.save(sponsor);
    }

    private void removeSponsoringFunds(Sponsor sponsor, Long competitionId) {
        Competition competition = competitionRepository.getById(competitionId);
        competition.setRaisedMoney(competition.getRaisedMoney() - sponsor.getSponsoringFunds());
        competition.getSponsors().remove(sponsor);
        competitionRepository.save(competition);
    }

    /**
     * When deleting a sponsor, each existent collaboration with a competition is updated
     */
    @Override
    public void deleteSponsor(Long id) {
        Sponsor sponsor = getById(id);
        sponsor.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .forEach(compId -> removeSponsoringFunds(sponsor, compId));

        sponsorRepository.delete(sponsor);
    }

}
