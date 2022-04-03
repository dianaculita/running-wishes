package com.project.controllers;

import com.project.dtos.CharityPersonDto;
import com.project.services.charity.CharityPersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
public class CharityPersonControllerTest {

    private static final String CNP = "2980603862299";

    @Mock
    private CharityPersonService charityPersonService;

    @InjectMocks
    private CharityPersonController charityPersonController;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(charityPersonService);
    }

    @Test
    public void testCharityPersonByCnp() {
        CharityPersonDto expectedPerson = new CharityPersonDto();
        expectedPerson.setPersonCnp(CNP);

        when(charityPersonService.getCharityPersonByCnp(CNP)).thenReturn(expectedPerson);

        CharityPersonDto actualPerson = charityPersonController.getCharityPersonByCnp(CNP);

        assertEquals(expectedPerson, actualPerson);

        verify(charityPersonService).getCharityPersonByCnp(anyString());
    }

    @Test
    public void testGetAllCharityPersons() {
        CharityPersonDto charityPersonDto = new CharityPersonDto();
        charityPersonDto.setPersonCnp(CNP);
        CharityPersonDto charityPersonDto1 = new CharityPersonDto();
        charityPersonDto1.setPersonCnp(CNP);
        List<CharityPersonDto> expectedPersons = Arrays.asList(charityPersonDto, charityPersonDto1);

        when(charityPersonService.getAllCharityPersons()).thenReturn(expectedPersons);

        List<CharityPersonDto> actualPersons = charityPersonController.getAllCharityPersons();

        assertEquals(expectedPersons, actualPersons);

        verify(charityPersonService).getAllCharityPersons();
    }

    @Test
    public void testCreateNewCharityPerson() {
        CharityPersonDto charityPersonDto = new CharityPersonDto();
        charityPersonDto.setPersonCnp(CNP);

        when(charityPersonService.createNewCharityPerson(charityPersonDto)).thenReturn(CNP);

        String actualCnp = charityPersonController.createNewCharityPerson(charityPersonDto);

        assertEquals(CNP, actualCnp);

        verify(charityPersonService).createNewCharityPerson(any(CharityPersonDto.class));
    }

    @Test
    public void testUpdateCharityPerson() {
        CharityPersonDto charityPersonDto = new CharityPersonDto();
        charityPersonDto.setPersonCnp(CNP);

        doNothing().when(charityPersonService).updateCharityPerson(charityPersonDto);

        charityPersonController.updateCharityPerson(charityPersonDto);

        verify(charityPersonService).updateCharityPerson(any(CharityPersonDto.class));
    }

    @Test
    public void testDeleteCharityPerson() {
        doNothing().when(charityPersonService).deleteCharityPerson(CNP);

        charityPersonController.deleteCharityPerson(CNP);

        verify(charityPersonService).deleteCharityPerson(anyString());
    }
}
