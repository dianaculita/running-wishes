package com.project.services.association;

import com.project.converters.ModelToDto;
import com.project.dtos.AssociationDto;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.repositories.AssociationRepository;
import com.project.repositories.CharityPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociationServiceImpl implements AssociationService {

    private final AssociationRepository associationRepository;

    private final CharityPersonRepository charityPersonRepository;

    @Autowired
    public AssociationServiceImpl(AssociationRepository associationRepository,
                                  CharityPersonRepository charityPersonRepository) {
        this.associationRepository = associationRepository;
        this.charityPersonRepository = charityPersonRepository;
    }

    @Override
    public AssociationDto getAssociationById(Long id) {
        return ModelToDto.associationToDto(getById(id));
    }

    private Association getById(Long id) {
        return associationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

    /**
     * When an association is deleted, all charity cases are moved to the first found association
     * If no other association exists, then the charity cases are deleted
     */
    @Override
    public void deleteAssociation(Long id) {
        Association association = getById(id);

        List<String> cnps = association.getCharityPeople().stream()
                .map(CharityPerson::getPersonCnp)
                .collect(Collectors.toList());
        if (cnps.size() > 0) {
            List<Association> associations = associationRepository.findAll();
            Association newAssociation;

            if (associations.size() > 1) { // check if the charity cases can
                // be transferred to any other association
                newAssociation = associations.stream()
                        .filter(a -> !a.equals(association))
                        .collect(Collectors.toList())
                        .get(0);

                updateCharityPeople(cnps, newAssociation);
                association.setCharityPeople(new ArrayList<>());
            } else {
                charityPersonRepository.deleteAll();
            }
        }

        associationRepository.delete(association);
    }

    private void updateCharityPeople(List<String> charityPeopleCnp, Association association) {
        charityPeopleCnp.forEach(cnp -> {
            CharityPerson charityPerson = charityPersonRepository.findByPersonCnp(cnp)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            charityPerson.setAssociation(association);
            charityPersonRepository.save(charityPerson);
        });
    }

}
