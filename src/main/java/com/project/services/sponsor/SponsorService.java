package com.project.services.sponsor;

import com.project.dtos.SponsorDto;
import com.project.dtos.UserDto;

import java.util.List;

public interface SponsorService {

    SponsorDto getSponsorById(Long id);

    List<SponsorDto> getAllSponsors();

    Long createNewSponsor(SponsorDto sponsorDto);

    void updateSponsor(SponsorDto sponsorDto);

    void deleteSponsor(Long id);
}
