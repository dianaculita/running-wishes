package com.project.services.donation;

import com.project.dtos.DonationDto;

import java.util.List;

public interface DonationService {

    DonationDto getDonationById(Long id);

    List<DonationDto> getAllDonations();

    Long createNewDonation(DonationDto donationDto);

    void deleteDonation(Long id);
}
