package com.project.controllers;

import com.project.dtos.SportDto;
import com.project.services.sport.SportServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DataJpaTest
public class SportControllerTest {

    private static final Long SPORT_ID = 100L;

    @Mock
    private SportServiceImpl sportService;

    @InjectMocks
    private SportController sportController;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(sportService);
    }

    @Test
    public void testGetSportById() {
        SportDto expectedSport = new SportDto();
        expectedSport.setSportId(SPORT_ID);

        when(sportService.getSportById(SPORT_ID)).thenReturn(expectedSport);

        SportDto actualSport = sportController.getSportById(SPORT_ID);

        assertEquals(expectedSport, actualSport);

        verify(sportService).getSportById(anyLong());
    }

    @Test
    public void testGetAllSports() {
        SportDto sportDto = new SportDto();
        sportDto.setSportId(SPORT_ID);
        SportDto sportDto2 = new SportDto();
        sportDto.setSportId(SPORT_ID);
        List<SportDto> expectedSports = Arrays.asList(sportDto, sportDto2);

        when(sportService.getAllSports()).thenReturn(expectedSports);

        List<SportDto> actualSports = sportController.getAllSports();

        assertEquals(expectedSports, actualSports);

        verify(sportService).getAllSports();
    }

    @Test
    public void testCreateNewSport() {
        SportDto sportDto = new SportDto();
        sportDto.setSportId(SPORT_ID);

        when(sportService.createNewSport(sportDto)).thenReturn(SPORT_ID);

        Long actualSportId = sportController.createNewSport(sportDto);

        assertEquals(SPORT_ID, actualSportId);

        verify(sportService).createNewSport(any());
    }

    @Test
    public void testUpdateSport() {
        SportDto sportDto = new SportDto();
        sportDto.setSportId(SPORT_ID);

        doNothing().when(sportService).updateSport(sportDto);

        sportController.updateSport(sportDto);

        verify(sportService).updateSport(any());
    }

    @Test
    public void testDeleteSport() {
        doNothing().when(sportService).deleteSport(SPORT_ID);

        sportController.deleteSport(SPORT_ID);

        verify(sportService).deleteSport(anyLong());
    }
}
