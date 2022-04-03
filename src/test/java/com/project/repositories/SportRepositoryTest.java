package com.project.repositories;

import com.project.models.Sport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SportRepositoryTest {

    @Autowired
    private SportRepository sportRepository;

    @AfterEach
    public void afterEach() {
        sportRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        Sport expectedSport = getSportMock("sport");
        Long sportId = sportRepository.save(expectedSport).getSportId();

        Optional<Sport> actualSport = sportRepository.findById(sportId);

        assertTrue(actualSport.isPresent());
        assertEquals(expectedSport, actualSport.get());
    }

    private Sport getSportMock(String name) {
        return Sport.builder()
                .name(name)
                .build();
    }

    @Test
    public void testGetAll() {
        Sport sport = getSportMock("sport1");
        Sport sport2 = getSportMock("sport2");
        List<Sport> expectedSports = Arrays.asList(sport, sport2);

        sportRepository.saveAll(expectedSports);

        List<Sport> actualSports = sportRepository.findAll();

        assertEquals(expectedSports, actualSports);
    }

    @Test
    public void testDelete() {
        Sport sport = getSportMock("sport");
        Long sportId = sportRepository.save(sport).getSportId();

        sportRepository.delete(sport);

        assertThrows(JpaObjectRetrievalFailureException.class, () -> sportRepository.getById(sportId));
    }

}
