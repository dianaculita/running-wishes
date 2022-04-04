package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.AssociationDto;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.repositories.AssociationRepository;
import com.project.repositories.CharityPersonRepository;
import com.project.services.association.AssociationServiceImpl;
import com.project.services.charity.CharityPersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DataJpaTest
public class AssociationServiceTest {

    private static final long ASSOCIATION_ID = 100L;

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private CharityPersonRepository charityPersonRepository;

    @Mock
    private CharityPersonService charityPersonService;

    @InjectMocks
    private AssociationServiceImpl associationService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(associationRepository);
    }

    @Test
    public void testGetAssociationById() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        AssociationDto expectedAssociation = ModelToDto.associationToDto(association);

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));

        AssociationDto actualAssociation = associationService.getAssociationById(ASSOCIATION_ID);

        verifyAssertions(expectedAssociation, actualAssociation);

        verify(associationRepository).findById(anyLong());
    }

    private void verifyAssertions(AssociationDto expectedAssociation, AssociationDto actualAssociation) {
        assertEquals(expectedAssociation.getAssociationId(), actualAssociation.getAssociationId());
        assertEquals(expectedAssociation.getName(), actualAssociation.getName());
        assertEquals(expectedAssociation.getPurpose(), actualAssociation.getPurpose());
    }

    private Association getAssociationMock(Long id, String name) {
        return Association.builder()
                .associationId(id)
                .name(name)
                .purpose("raise money for sick children")
                .charityPeople(new ArrayList<>())
                .build();
    }

    @Test
    public void testGetAssociationById_shouldReturnNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(associationRepository).findById(ASSOCIATION_ID);

        assertThrows(ResponseStatusException.class, () -> associationService.getAssociationById(ASSOCIATION_ID));

        verify(associationRepository).findById(anyLong());
    }

    @Test
    public void testGetAllAssociations() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        Association association2 = getAssociationMock(ASSOCIATION_ID + 1L, "Together");

        List<Association> associations = Arrays.asList(association, association2);

        AssociationDto associationDto = ModelToDto.associationToDto(association);
        AssociationDto associationDto2 = ModelToDto.associationToDto(association2);

        List<AssociationDto> expectedAssociations = Arrays.asList(associationDto, associationDto2);

        when(associationRepository.findAll()).thenReturn(associations);

        List<AssociationDto> actualAssociations = associationService.getAllAssociations();

        assertEquals(expectedAssociations.size(), actualAssociations.size());
        verifyAssertions(expectedAssociations.get(0), actualAssociations.get(0));
        verifyAssertions(expectedAssociations.get(1), actualAssociations.get(1));

        verify(associationRepository).findAll();
    }

    @Test
    public void testCreateNewAssociation() {
        Association association = new Association();
        AssociationDto associationDto = new AssociationDto();

        when(associationRepository.save(any(Association.class))).thenReturn(association);

        associationService.createNewAssociation(associationDto);

        verify(associationRepository).save(any(Association.class));
    }

    @Test
    public void testUpdateAssociation() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        AssociationDto associationDto = ModelToDto.associationToDto(association);
        associationDto.setPurpose("");

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
        when(associationRepository.save(any(Association.class))).thenReturn(association);

        associationService.updateAssociation(associationDto);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).save(any(Association.class));
    }

    @Test
    public void testUpdateAssociation_shouldThrowNotFoundException() {
        AssociationDto associationDto = AssociationDto.builder().associationId(ASSOCIATION_ID).build();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(associationRepository).findById(ASSOCIATION_ID);

        assertThrows(ResponseStatusException.class, () -> associationService.updateAssociation(associationDto));

        verify(associationRepository).findById(anyLong());
    }

    @Test
    public void testDeleteAssociation_whenNoCharityPeople() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        association.setCharityPeople(new ArrayList<>());

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.ofNullable(association));
        doNothing().when(associationRepository).delete(association);

        associationService.deleteAssociation(ASSOCIATION_ID);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).delete(any(Association.class));
    }

    @Disabled
    @Test
    public void testDeleteAssociation_withCharityPeople() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        CharityPerson charityPerson = CharityPerson.builder()
                .personCnp("x")
                .association(association)
                .build();
        association.setCharityPeople(List.of(charityPerson));

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.ofNullable(association));
        when(associationRepository.findAll()).thenReturn(List.of(association));
        doNothing().when(charityPersonService).deleteCharityPerson("x");
        when(associationRepository.save(any(Association.class))).thenReturn(association);
        doNothing().when(associationRepository).delete(association);

        associationService.deleteAssociation(ASSOCIATION_ID);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).findAll();
        verify(charityPersonService).deleteCharityPerson(anyString());
        verify(associationRepository).save(any(Association.class));
        verify(associationRepository).delete(any(Association.class));
    }

    @Test
    public void testDeleteAssociation_withAvailableAssociations() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");
        Association association2 = getAssociationMock(ASSOCIATION_ID + 1L, "Together");
        CharityPerson charityPerson = CharityPerson.builder()
                .personCnp("x")
                .association(association)
                .build();
        association.setCharityPeople(List.of(charityPerson));
        association2.setCharityPeople(new ArrayList<>());

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
        when(associationRepository.findAll()).thenReturn(Arrays.asList(association, association2));
        when(charityPersonRepository.findByPersonCnp("x")).thenReturn(Optional.of(charityPerson));
        when(charityPersonRepository.save(any(CharityPerson.class))).thenReturn(charityPerson);
        doNothing().when(associationRepository).delete(association);

        associationService.deleteAssociation(ASSOCIATION_ID);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).findAll();
        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).save(any(CharityPerson.class));
        verify(associationRepository).delete(any(Association.class));
    }

    @Test
    public void testDeleteAssociation_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(associationRepository).findById(ASSOCIATION_ID);

        assertThrows(ResponseStatusException.class, () -> associationService.deleteAssociation(ASSOCIATION_ID));

        verify(associationRepository).findById(anyLong());
    }

}
