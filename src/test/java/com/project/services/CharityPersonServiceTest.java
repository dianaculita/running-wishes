package com.project.services;

import com.project.converters.ModelToDto;
import com.project.dtos.CharityPersonDto;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.repositories.CharityPersonRepository;
import com.project.services.association.AssociationServiceImpl;
import com.project.services.charity.CharityPersonServiceImpl;
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
public class CharityPersonServiceTest {

    public static final String PERSON_CNP = "5180101253377";
    public static final String PERSON2_CNP = "6180101253277";
    public static final long ASSOCIATION_ID = 100L;

    @Mock
    private CharityPersonRepository charityPersonRepository;

    @Mock
    private AssociationServiceImpl associationService;

    @InjectMocks
    private CharityPersonServiceImpl charityPersonService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(charityPersonRepository);
    }

    @Test
    public void testGetCharityPersonByCnp() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Josh", 1234L, association, PERSON_CNP);

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.ofNullable(charityPerson));

        CharityPersonDto expectedCharityPerson = ModelToDto.charityPersonToDto(charityPerson);

        CharityPersonDto actualCharityPerson = charityPersonService.getCharityPersonByCnp(PERSON_CNP);

        verifyAssertions(expectedCharityPerson, actualCharityPerson);

        verify(charityPersonRepository).findByPersonCnp(anyString());
    }

    private void verifyAssertions(CharityPersonDto expectedCharityPerson, CharityPersonDto actualCharityPerson) {
        assertEquals(expectedCharityPerson.getAssociationId(), actualCharityPerson.getAssociationId());
        assertEquals(expectedCharityPerson.getName(), actualCharityPerson.getName());
        assertEquals(expectedCharityPerson.getStory(), actualCharityPerson.getStory());
        assertEquals(expectedCharityPerson.getIban(), actualCharityPerson.getIban());
        assertEquals(expectedCharityPerson.getAge(), actualCharityPerson.getAge());
        assertEquals(expectedCharityPerson.getNeededFund(), actualCharityPerson.getNeededFund());
        assertEquals(expectedCharityPerson.getRaisedFund(), actualCharityPerson.getRaisedFund());
    }

    private CharityPerson getCharityPersonMock(String name, Long iban, Association association, String cnp) {
        CharityPerson charityPerson = CharityPerson.builder()
                .association(association)
                .name(name)
                .story("abandoned")
                .iban(iban)
                .age(3)
                .personCnp(cnp)
                .neededFund(1000.0)
                .raisedFund(0.0)
                .build();
        return charityPerson;
    }

    @Test
    public void testGetCharityPersonByCnp_shouldThrowNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(charityPersonRepository).findByPersonCnp("someCnp");

        assertThrows(ResponseStatusException.class, () -> charityPersonService.getCharityPersonByCnp("someCnp"));

        verify(charityPersonRepository).findByPersonCnp(anyString());
    }

    @Test
    public void testGetAllCharityPersons() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Josh", 1234L, association, PERSON_CNP);
        CharityPerson charityPerson2 = getCharityPersonMock("Jane", 1834L, association, PERSON2_CNP);

        List<CharityPerson> charityPersons = Arrays.asList(charityPerson, charityPerson2);

        CharityPersonDto charityPersonDto = ModelToDto.charityPersonToDto(charityPerson);
        CharityPersonDto charityPersonDto2 = ModelToDto.charityPersonToDto(charityPerson2);

        List<CharityPersonDto> expectedCharityPersons = Arrays.asList(charityPersonDto, charityPersonDto2);

        when(charityPersonRepository.findAll()).thenReturn(charityPersons);

        List<CharityPersonDto> actualCharityPeople = charityPersonService.getAllCharityPersons();

        assertEquals(expectedCharityPersons.size(), actualCharityPeople.size());
        verifyAssertions(expectedCharityPersons.get(0), actualCharityPeople.get(0));
        verifyAssertions(expectedCharityPersons.get(1), actualCharityPeople.get(1));

        verify(charityPersonRepository).findAll();
    }

    @Test
    public void testCreateNewCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        CharityPersonDto charityPersonDto = ModelToDto.charityPersonToDto(charityPerson);

        when(associationService.getAssociationById(ASSOCIATION_ID)).thenReturn(ModelToDto.associationToDto(association));
        when(charityPersonRepository.save(any())).thenReturn(charityPerson);

        charityPersonService.createNewCharityPerson(charityPersonDto);

        verify(charityPersonRepository).save(any());
    }

    @Test
    public void testUpdateNewCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        CharityPersonDto charityPersonDto = ModelToDto.charityPersonToDto(charityPerson);

        when(associationService.getAssociationById(ASSOCIATION_ID)).thenReturn(ModelToDto.associationToDto(association));
        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.of(charityPerson));
        when(charityPersonRepository.save(any())).thenReturn(charityPerson);

        charityPersonService.updateCharityPerson(charityPersonDto);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).save(any());
    }

    @Test
    public void testUpdateNewCharityPerson_shouldThrowNotFoundException() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        CharityPersonDto charityPersonDto = ModelToDto.charityPersonToDto(charityPerson);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(charityPersonRepository).findByPersonCnp(PERSON_CNP);

        assertThrows(ResponseStatusException.class, () -> charityPersonService.updateCharityPerson(charityPersonDto));

        verify(charityPersonRepository).findByPersonCnp(anyString());
    }

    @Test
    public void testDeleteCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.of(charityPerson));
        doNothing().when(charityPersonRepository).delete(charityPerson);

        charityPersonService.deleteCharityPerson(PERSON_CNP);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).delete(any());
    }

    @Test
    public void testDeleteCharityPerson_shouldThrowNotFoundException() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(charityPersonRepository).delete(charityPerson);

        assertThrows(ResponseStatusException.class, () -> charityPersonService.deleteCharityPerson(PERSON_CNP));

        verify(charityPersonRepository).findByPersonCnp(anyString());
    }
}
