package com.project.controllers;

import com.project.dtos.AssociationDto;
import com.project.services.association.AssociationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DataJpaTest
public class AssociationControllerTest {

    private static final long ASSOCIATION_ID = 100L;

    @Mock
    private AssociationServiceImpl associationService;

    @InjectMocks
    private AssociationController associationController;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(associationService);
    }

    @Test
    public void testGetAssociationById() {
        AssociationDto expectedAssociation = new AssociationDto();
        expectedAssociation.setAssociationId(ASSOCIATION_ID);

        when(associationService.getAssociationById(ASSOCIATION_ID)).thenReturn(expectedAssociation);

        AssociationDto actualAssociation = associationController.getAssociationById(ASSOCIATION_ID);

        assertEquals(expectedAssociation, actualAssociation);

        verify(associationService).getAssociationById(anyLong());
    }

    @Test
    public void testGetAllAssociations() {
        AssociationDto associationDto = new AssociationDto();
        AssociationDto associationDto2 = new AssociationDto();
        associationDto.setAssociationId(ASSOCIATION_ID);
        associationDto2.setAssociationId(ASSOCIATION_ID + 1L);
        List<AssociationDto> expectedAssociations = Arrays.asList(associationDto, associationDto2);

        when(associationService.getAllAssociations()).thenReturn(expectedAssociations);

        List<AssociationDto> actualAssociations = associationController.getAllAssociations();

        assertEquals(expectedAssociations, actualAssociations);

        verify(associationService).getAllAssociations();
    }

    @Test
    public void testCreateNewAssociation() {
        AssociationDto associationDto = new AssociationDto();
        associationDto.setAssociationId(ASSOCIATION_ID);

        when(associationService.createNewAssociation(associationDto)).thenReturn(ASSOCIATION_ID);

        Long actualId = associationController.createNewAssociation(associationDto);

        assertEquals(ASSOCIATION_ID, actualId);

        verify(associationService).createNewAssociation(any(AssociationDto.class));
    }

    @Test
    public void testUpdateAssociation() {
        AssociationDto associationDto = new AssociationDto();
        associationDto.setAssociationId(ASSOCIATION_ID);

        doNothing().when(associationService).updateAssociation(associationDto);

        associationController.updateAssociation(associationDto);

        verify(associationService).updateAssociation(any(AssociationDto.class));
    }

    @Test
    public void testDeleteAssociation() {
        doNothing().when(associationService).deleteAssociation(ASSOCIATION_ID);

        associationController.deleteAssociation(ASSOCIATION_ID);

        verify(associationService).deleteAssociation(anyLong());
    }


}
