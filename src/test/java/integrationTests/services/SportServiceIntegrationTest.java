package integrationTests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.dtos.SportDto;
import com.project.mappers.SportMapper;
import com.project.mappers.SportMapperImpl;
import com.project.models.Sport;
import com.project.repositories.SportRepository;
import com.project.services.sport.SportService;
import com.project.services.sport.SportServiceImpl;
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
@ContextConfiguration(classes = {SportMapperImpl.class, SportServiceImpl.class})
public class SportServiceIntegrationTest {

    private static final long SPORT_ID = 100L;

    @MockBean
    private SportRepository sportRepository;

    @SpyBean
    private SportMapper sportMapper;

    @Autowired
    private SportService sportService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(sportRepository, sportMapper);
    }

    @Test
    public void testGetSportById() {
        Sport sport = getSportMock(SPORT_ID, "running");

        when(sportRepository.findById(SPORT_ID)).thenReturn(Optional.of(sport));
        SportDto actualSport = sportService.getSportById(SPORT_ID);

        assertEquals(sport.getName(), actualSport.getName());
        assertEquals(sport.getSportId(), actualSport.getSportId());

        verify(sportRepository).findById(anyLong());
        verify(sportMapper).sportEntityToDto(any());
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

        when(sportRepository.findAll()).thenReturn(sports);

        List<SportDto> actualSports = sportService.getAllSports();

        assertEquals(2, actualSports.size());
        assertEquals(sport.getName(), actualSports.get(0).getName());
        assertEquals(sport2.getName(), actualSports.get(1).getName());

        verify(sportRepository).findAll();
        verify(sportMapper, times(2)).sportEntityToDto(any());
    }

    @Test
    public void testCreateNewSport() {
        Sport sport = getSportMock(SPORT_ID, "running");

        when(sportRepository.save(any(Sport.class))).thenReturn(sport);

        sportService.createNewSport(SportDto.builder().name(sport.getName()).build());

        verify(sportRepository).save(any(Sport.class));
    }

}
