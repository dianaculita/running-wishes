package com.project.services.association;

import com.project.dtos.AssociationDto;

import java.util.List;

public interface AssociationService {

    AssociationDto getAssociationById(Long id);

    List<AssociationDto> getAllAssociations();

    Long createNewAssociation(AssociationDto associationDto);

    void updateAssociation(AssociationDto associationDto);

    void deleteAssociation(Long id);
}
