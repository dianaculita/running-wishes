package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.AssociationDto;
import com.project.models.Association;
import com.project.repositories.AssociationRepository;
import com.project.services.association.AssociationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @InjectMocks
    private AssociationServiceImpl associationService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(associationRepository);
    }

    @Test
    public void testGetAssociationById() {
        Association association = getAssociationMock();
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

    private Association getAssociationMock() {
        return Association.builder()
                .associationId(ASSOCIATION_ID)
                .name("People together")
                .purpose("raise money for sick children")
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
        Association association = Association.builder()
                .associationId(ASSOCIATION_ID)
                .name("People together")
                .purpose("raise money for sick children")
                .build();

        Association association2 = Association.builder()
                .associationId(ASSOCIATION_ID + 1L)
                .name("People")
                .purpose("raise money for sick children")
                .build();

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
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        AssociationDto associationDto = ModelToDto.associationToDto(association);

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
    public void testDeleteAssociation() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.ofNullable(association));
        doNothing().when(associationRepository).delete(association);

        associationService.deleteAssociation(ASSOCIATION_ID);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).delete(any(Association.class));
    }

    @Test
    public void testDeleteAssociation_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(associationRepository).findById(ASSOCIATION_ID);

        assertThrows(ResponseStatusException.class, () -> associationService.deleteAssociation(ASSOCIATION_ID));

        verify(associationRepository).findById(anyLong());
    }

}
