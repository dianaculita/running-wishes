package com.project.controllers;

import com.project.dtos.ParticipantDto;
import com.project.services.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participant")
public class ParticipantController {

    private final ParticipantService participantService;

    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping(value = "/{cnp}")
    public ParticipantDto getParticipantById(@PathVariable String cnp) {
        return participantService.getParticipantByCnp(cnp);
    }

    @GetMapping(value = "/getAll")
    public List<ParticipantDto> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @PostMapping
    public String createNewParticipant(@RequestBody ParticipantDto participantDto) {
        return participantService.createNewParticipant(participantDto);
    }

    @PutMapping
    public void updateParticipant(@RequestBody ParticipantDto participantDto) {
        participantService.updateParticipant(participantDto);
    }

    @DeleteMapping(value = "/{cnp}")
    public void deleteParticipant(@PathVariable String cnp) {
        participantService.deleteParticipant(cnp);
    }
}
