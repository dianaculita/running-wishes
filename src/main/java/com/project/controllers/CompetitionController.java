package com.project.controllers;

import com.project.dtos.CompetitionDto;
import com.project.services.competition.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @Autowired
    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping(value = "/{id}")
    public CompetitionDto getCompetitionById(@PathVariable Long id) {
        return competitionService.getCompetitionById(id);
    }

    @GetMapping(value = "/getAll")
    public List<CompetitionDto> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

    @PostMapping
    public Long createNewCompetition(@RequestBody CompetitionDto competitionDto) {
        return competitionService.createNewCompetition(competitionDto);
    }

    @PutMapping
    public void updateCompetition(@RequestBody CompetitionDto competitionDto) {
        competitionService.updateCompetition(competitionDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCompetition(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
    }
}
