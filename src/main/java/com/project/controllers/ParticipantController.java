package com.project.controllers;

import com.project.dtos.ParticipantDto;
import com.project.services.participant.ParticipantService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participant")
@Tag(name = "Competition Participant")
public class ParticipantController {

    private final ParticipantService participantService;

    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/{cnp}")
    public ParticipantDto getParticipantById(@PathVariable String cnp) {
        return participantService.getParticipantByCnp(cnp);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/getAll")
    public List<ParticipantDto> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public String createNewParticipant(@RequestBody ParticipantDto participantDto) {
        return participantService.createNewParticipant(participantDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping
    public void updateParticipant(@RequestBody ParticipantDto participantDto) {
        participantService.updateParticipant(participantDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping(value = "/{cnp}")
    public void deleteParticipant(@PathVariable String cnp) {
        participantService.deleteParticipant(cnp);
    }
}
