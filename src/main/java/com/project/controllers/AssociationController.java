package com.project.controllers;

import com.project.dtos.AssociationDto;
import com.project.services.association.AssociationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/association")
@Tag(name = "Association")
public class AssociationController {

    private final AssociationService associationService;

    @Autowired
    public AssociationController(AssociationService associationService) {
        this.associationService = associationService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/{id}")
    public AssociationDto getAssociationById(@PathVariable Long id) {
        return associationService.getAssociationById(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/getAll")
    public List<AssociationDto> getAllAssociations() {
        return associationService.getAllAssociations();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public Long createNewAssociation(@RequestBody AssociationDto associationDto) {
        return associationService.createNewAssociation(associationDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping
    public void updateAssociation(@RequestBody AssociationDto associationDto) {
        associationService.updateAssociation(associationDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping(value = "/{id}")
    public void deleteAssociation(@PathVariable Long id) {
        associationService.deleteAssociation(id);
    }
}
