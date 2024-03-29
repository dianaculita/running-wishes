package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
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

import com.project.dtos.CharityPersonDto;
import com.project.mappers.CharityPersonMapper;
import com.project.mappers.CharityPersonMapperImpl;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.models.Donation;
import com.project.repositories.AssociationRepository;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.DonationRepository;
import com.project.services.charity.CharityPersonService;
import com.project.services.charity.CharityPersonServiceImpl;
import org.junit.jupiter.api.AfterEach;
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
@ContextConfiguration(classes = {CharityPersonMapperImpl.class, CharityPersonServiceImpl.class})
public class CharityPersonServiceIntegrationTest {

    private static final String PERSON_CNP = "5180101253377";
    private static final String PERSON2_CNP = "6180101253277";
    private static final long ASSOCIATION_ID = 100L;

    @MockBean
    private CharityPersonRepository charityPersonRepository;

    @MockBean
    private AssociationRepository associationRepository;

    @MockBean
    private DonationRepository donationRepository;

    @SpyBean
    private CharityPersonMapper charityPersonMapper;

    @Autowired
    private CharityPersonService charityPersonService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(charityPersonRepository, charityPersonMapper);

        charityPersonRepository.deleteAll();
    }

    @Test
    public void testGetCharityPersonByCnp() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Josh", 1234L, association, PERSON_CNP);

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.ofNullable(charityPerson));

        CharityPersonDto actualCharityPerson = charityPersonService.getCharityPersonByCnp(PERSON_CNP);

        verifyAssertions(charityPerson.getAssociation().getAssociationId(), charityPerson.getName(), charityPerson.getStory(),
              charityPerson.getIban(), charityPerson.getNeededFund(), charityPerson.getRaisedFund(), actualCharityPerson);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonMapper).personEntityToDto(any());
    }

    private void verifyAssertions(Long assocId, String name, String story, Long iban, Double needed, Double raised, CharityPersonDto actualCharityPerson) {
        assertEquals(assocId, actualCharityPerson.getAssociationId());
        assertEquals(name, actualCharityPerson.getName());
        assertEquals(story, actualCharityPerson.getStory());
        assertEquals(iban, actualCharityPerson.getIban());
        assertEquals(needed, actualCharityPerson.getNeededFund());
        assertEquals(raised, actualCharityPerson.getRaisedFund());
    }

    private CharityPerson getCharityPersonMock(String name, Long iban, Association association, String cnp) {
        return CharityPerson.builder()
                .association(association)
                .name(name)
                .story("abandoned")
                .iban(iban)
                .personCnp(cnp)
                .neededFund(1000.0)
                .raisedFund(0.0)
                .build();
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

        when(charityPersonRepository.findAll()).thenReturn(charityPersons);

        List<CharityPersonDto> actualCharityPeople = charityPersonService.getAllCharityPersons();

        assertEquals(2, actualCharityPeople.size());
        verifyAssertions(charityPerson.getAssociation().getAssociationId(), charityPerson.getName(), charityPerson.getStory(),
              charityPerson.getIban(), charityPerson.getNeededFund(), charityPerson.getRaisedFund(), actualCharityPeople.get(0));
        verifyAssertions(charityPerson2.getAssociation().getAssociationId(), charityPerson2.getName(), charityPerson2.getStory(),
              charityPerson2.getIban(), charityPerson2.getNeededFund(), charityPerson2.getRaisedFund(), actualCharityPeople.get(1));

        verify(charityPersonRepository).findAll();
        verify(charityPersonMapper, times(2)).personEntityToDto(any());
    }

    @Test
    public void testCreateNewCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        association.setCharityPeople(new ArrayList<>());
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        CharityPersonDto charityPersonDto = CharityPersonDto.builder()
              .name(charityPerson.getName())
              .iban(charityPerson.getIban())
              .associationId(ASSOCIATION_ID)
              .personCnp(PERSON_CNP)
              .build();

        when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
        when(charityPersonRepository.save(any(CharityPerson.class))).thenReturn(charityPerson);
        when(associationRepository.save(any(Association.class))).thenReturn(association);

        charityPersonService.createNewCharityPerson(charityPersonDto);

        verify(associationRepository).findById(anyLong());
        verify(associationRepository).save(any(Association.class));
        verify(charityPersonRepository).save(any(CharityPerson.class));
    }

    @Test
    public void testUpdateNewCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        CharityPersonDto charityPersonDto = CharityPersonDto.builder()
                                                            .personCnp(PERSON_CNP)
                                                            .name(charityPerson.getName())
                                                            .associationId(ASSOCIATION_ID + 1)
                                                            .build();

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.of(charityPerson));
        when(charityPersonRepository.save(any(CharityPerson.class))).thenReturn(charityPerson);

        charityPersonService.updateCharityPerson(charityPersonDto);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).save(any(CharityPerson.class));
    }

    @Test
    public void testUpdateNewCharityPerson_shouldThrowNotFoundException() {
        CharityPersonDto charityPersonDto = CharityPersonDto.builder().personCnp("someCnp").build();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(charityPersonRepository).findByPersonCnp(PERSON_CNP);

        assertThrows(ResponseStatusException.class, () -> charityPersonService.updateCharityPerson(charityPersonDto));

        verify(charityPersonRepository).findByPersonCnp(anyString());
    }

    @Test
    public void testDeleteCharityPerson() {
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        charityPerson.setDonations(new ArrayList<>());

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.of(charityPerson));
        doNothing().when(charityPersonRepository).delete(charityPerson);

        charityPersonService.deleteCharityPerson(PERSON_CNP);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(charityPersonRepository).delete(any(CharityPerson.class));
    }

    @Test
    public void testDeleteCharityPersonWithDonations() {
        Donation donation = new Donation();
        Association association = Association.builder().associationId(ASSOCIATION_ID).build();
        CharityPerson charityPerson = getCharityPersonMock("Mike", 1L, association, PERSON_CNP);
        charityPerson.setDonations(List.of(donation));

        when(charityPersonRepository.findByPersonCnp(PERSON_CNP)).thenReturn(Optional.of(charityPerson));
        doNothing().when(donationRepository).deleteAll(List.of(donation));
        doNothing().when(charityPersonRepository).delete(charityPerson);

        charityPersonService.deleteCharityPerson(PERSON_CNP);

        verify(charityPersonRepository).findByPersonCnp(anyString());
        verify(donationRepository).deleteAll(anyList());
        verify(charityPersonRepository).delete(any(CharityPerson.class));
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
