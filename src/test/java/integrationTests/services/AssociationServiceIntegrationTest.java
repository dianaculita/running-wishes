package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.dtos.AssociationDto;
import com.project.mappers.AssociationMapper;
import com.project.mappers.AssociationMapperImpl;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.repositories.AssociationRepository;
import com.project.repositories.CharityPersonRepository;
import com.project.services.association.AssociationService;
import com.project.services.association.AssociationServiceImpl;
import com.project.services.charity.CharityPersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AssociationMapperImpl.class, AssociationServiceImpl.class})
public class AssociationServiceIntegrationTest {

    private static final long ASSOCIATION_ID = 100L;

    @MockBean
    private AssociationRepository associationRepository;

    @MockBean
    private CharityPersonRepository charityPersonRepository;

    @MockBean
    private CharityPersonService charityPersonService;

    @SpyBean
    private AssociationMapper associationMapper;

    @Autowired
    private AssociationService associationService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(associationRepository, charityPersonRepository,
              charityPersonService, associationMapper);
    }

    @Test
    public void testGetAssociationById() {
        Association association = getAssociationMock(ASSOCIATION_ID, "People together");

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));

        AssociationDto actualAssociation = associationService.getAssociationById(ASSOCIATION_ID);

        verifyAssertions(association.getAssociationId(), association.getName(), association.getPurpose(), actualAssociation);

        verify(associationRepository).findById(anyLong());
        verify(associationMapper).associationEntityToDto(any());
    }

    private void verifyAssertions(Long id, String name, String purpose, AssociationDto actualAssociation) {
        assertEquals(id, actualAssociation.getAssociationId());
        assertEquals(name, actualAssociation.getName());
        assertEquals(purpose, actualAssociation.getPurpose());
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

        when(associationRepository.findAll()).thenReturn(associations);

        List<AssociationDto> actualAssociations = associationService.getAllAssociations();

        assertEquals(2, actualAssociations.size());
        verifyAssertions(association.getAssociationId(), association.getName(), association.getPurpose(), actualAssociations.get(0));
        verifyAssertions(association2.getAssociationId(), association2.getName(), association2.getPurpose(), actualAssociations.get(1));

        verify(associationRepository).findAll();
        verify(associationMapper, times(2)).associationEntityToDto(any());
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

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
        when(associationRepository.save(any(Association.class))).thenReturn(association);

        associationService.updateAssociation(AssociationDto.builder()
                                .associationId(ASSOCIATION_ID)
                                .name(association.getName())
                                .purpose(association.getPurpose())
                                .build());

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
