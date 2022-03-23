package com.project.services.association;

import com.project.converters.ModelToDto;
import com.project.dtos.AssociationDto;
import com.project.models.Association;
import com.project.repositories.AssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociationServiceImpl implements AssociationService {

    private final AssociationRepository associationRepository;

    @Autowired
    public AssociationServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Override
    public AssociationDto getAssociationById(Long id) {
        return ModelToDto.associationToDto(getById(id));
    }

    @Override
    public List<AssociationDto> getAllAssociations() {
        return associationRepository.findAll().stream()
                .map(ModelToDto::associationToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long createNewAssociation(AssociationDto associationDto) {
        Association association = Association.builder()
                .name(associationDto.getName())
                .purpose(associationDto.getPurpose())
                .build();

        return associationRepository.save(association).getAssociationId();
    }

    @Override
    public void updateAssociation(AssociationDto associationDto) {
        Association association = getById(associationDto.getAssociationId());

        association.setName(associationDto.getName());
        association.setPurpose(associationDto.getPurpose());

        associationRepository.save(association);
    }

    @Override
    public void deleteAssociation(Long id) {
        associationRepository.delete(getById(id));
    }

    private Association getById(Long id) {
        return associationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
