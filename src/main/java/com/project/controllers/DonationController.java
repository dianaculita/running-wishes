package com.project.controllers;

import com.project.dtos.DonationDto;
import com.project.services.donation.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donation")
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping(value = "/{id}")
    public DonationDto getDonationById(@PathVariable Long id) {
        return donationService.getDonationById(id);
    }

    @GetMapping(value = "/getAll")
    public List<DonationDto> getAllDonations() {
       return donationService.getAllDonations();
    }

    @PostMapping
    public Long createNewDonation(@RequestBody DonationDto donationDto) {
        return donationService.createNewDonation(donationDto);
    }

    @PutMapping
    void updateDonation(@RequestBody DonationDto donationDto) {
        donationService.updateDonation(donationDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
    }
}
