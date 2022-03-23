package com.project.controllers;

import com.project.dtos.SportDto;
import com.project.services.sport.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sport")
public class SportController {

    private final SportService sportService;

    @Autowired
    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping(value = "/{id}")
    public SportDto getSportById(@PathVariable Long id) {
        return sportService.getSportById(id);
    }

    @GetMapping(value = "/getAll")
    public List<SportDto> getAllSports() {
        return sportService.getAllSports();
    }

    @PostMapping
    public Long createNewSport(@RequestBody SportDto sportDto) {
        return sportService.createNewSport(sportDto);
    }

    @PutMapping
    public void updateSport(@RequestBody SportDto sportDto) {
        sportService.updateSport(sportDto);
    }

    @DeleteMapping(value = "/{id}")
    void deleteSport(@PathVariable Long id) {
        sportService.deleteSport(id);
    }
}
