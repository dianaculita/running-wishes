package com.project.controllers;

import com.project.dtos.SponsorDto;
import com.project.services.sponsor.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sponsor")
public class SponsorController {

    private final SponsorService sponsorService;

    @Autowired
    public SponsorController(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @GetMapping(value = "/{id}")
    public SponsorDto getSponsorById(@PathVariable Long id) {
        return sponsorService.getSponsorById(id);
    }

    @GetMapping(value = "/getAll")
    public List<SponsorDto> getAllSponsors() {
        return sponsorService.getAllSponsors();
    }

    @PostMapping
    public Long createNewSponsor(@RequestBody SponsorDto sponsorDto) {
        return sponsorService.createNewSponsor(sponsorDto);
    }

    @PutMapping
    public void updateSponsor(@RequestBody SponsorDto sponsorDto) {
        sponsorService.updateSponsor(sponsorDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteSponsor(@PathVariable Long id) {
        sponsorService.deleteSponsor(id);
    }
}
