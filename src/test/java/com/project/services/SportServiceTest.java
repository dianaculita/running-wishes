package com.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.converters.ModelToDto;
import com.project.dtos.SportDto;
import com.project.models.Sport;
import com.project.repositories.SportRepository;
import com.project.services.sport.SportServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class SportServiceTest {

    private static final long SPORT_ID = 100L;

    @Mock
    private SportRepository sportRepository;

    @InjectMocks
    private SportServiceImpl sportService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(sportRepository);
    }

    @Test
    public void testGetSportById() {
        Sport sport = getSportMock(SPORT_ID, "running");

        SportDto expectedSport = ModelToDto.sportToDto(sport);

        when(sportRepository.findById(SPORT_ID)).thenReturn(Optional.of(sport));

        SportDto actualSport = sportService.getSportById(SPORT_ID);

        assertEquals(expectedSport.getName(), actualSport.getName());

        verify(sportRepository).findById(anyLong());
    }

    private Sport getSportMock(Long id, String name) {
        return Sport.builder()
                .sportId(id)
                .name(name)
                .build();
    }

    @Test
    public void testGetSportById_shouldReturnNotFoundException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(sportRepository).findById(SPORT_ID);

        assertThrows(ResponseStatusException.class, () -> sportService.getSportById(SPORT_ID));

        verify(sportRepository).findById(SPORT_ID);
    }

    @Test
    public void testGetAllSports() {
        Sport sport = getSportMock(SPORT_ID, "running");
        Sport sport2 = getSportMock(SPORT_ID + 1L, "swimming");
        List<Sport> sports = Arrays.asList(sport, sport2);

        SportDto sportDto = ModelToDto.sportToDto(sport);
        SportDto sportDto2 = ModelToDto.sportToDto(sport2);
        List<SportDto> expectedSports = Arrays.asList(sportDto, sportDto2);

        when(sportRepository.findAll()).thenReturn(sports);

        List<SportDto> actualSports = sportService.getAllSports();

        assertEquals(expectedSports.size(), actualSports.size());
        assertEquals(expectedSports.get(0).getName(), actualSports.get(0).getName());
        assertEquals(expectedSports.get(1).getName(), actualSports.get(1).getName());

        verify(sportRepository).findAll();
    }

    @Test
    public void testCreateNewSport() {
        Sport sport = getSportMock(SPORT_ID, "running");
        SportDto sportDto = ModelToDto.sportToDto(sport);

        when(sportRepository.save(any(Sport.class))).thenReturn(sport);

        sportService.createNewSport(sportDto);

        verify(sportRepository).save(any(Sport.class));
    }

}
