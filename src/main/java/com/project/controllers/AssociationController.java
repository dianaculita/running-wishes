package com.project.controllers;

import com.project.dtos.AssociationDto;
import com.project.services.association.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/association")
public class AssociationController {

    private final AssociationService associationService;

    @Autowired
    public AssociationController(AssociationService associationService) {
        this.associationService = associationService;
    }

    @GetMapping(value = "/{id}")
    public AssociationDto getAssociationById(@PathVariable Long id) {
        return associationService.getAssociationById(id);
    }

    @GetMapping(value = "/getAll")
    public List<AssociationDto> getAllAssociations() {
        return associationService.getAllAssociations();
    }

    @PostMapping
    public Long createNewAssociation(@RequestBody AssociationDto associationDto) {
        return associationService.createNewAssociation(associationDto);
    }

    @PutMapping
    public void updateAssociation(@RequestBody AssociationDto associationDto) {
        associationService.updateAssociation(associationDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteAssociation(@PathVariable Long id) {
        associationService.deleteAssociation(id);
    }
}
