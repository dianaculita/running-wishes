package com.project.repositories;

import com.project.models.Competition;
import com.project.models.Sport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;
    private Sport sport;

    @Autowired
    private SportRepository sportRepository;

    @AfterEach
    public void afterEach() {
        competitionRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        sport = Sport.builder().name("run").build();
        sportRepository.save(sport);
    }

    @Test
    public void testFindById() {
        Competition expectedCompetition = getCompetitionMock("Color run");
        expectedCompetition.setSport(sport);

        Long competitionId = competitionRepository.save(expectedCompetition).getCompetitionId();

        Optional<Competition> actualCompetition = competitionRepository.findById(competitionId);

        assertTrue(actualCompetition.isPresent());
        assertEquals(expectedCompetition, actualCompetition.get());
    }

    private Competition getCompetitionMock(String name) {
        return Competition.builder()
                .name(name)
                .ticketFee(30.0)
                .raisedMoney(0.0)
                .numberOfDays(2)
                .location("Bucharest")
                .build();
    }

    @Test
    public void testGetAll() {
        Competition competition = getCompetitionMock("Color run");
        competition.setSport(sport);
        Competition competition1 = getCompetitionMock("Run");
        competition1.setSport(sport);
        List<Competition> expectedCompetitions = Arrays.asList(competition, competition1);

        competitionRepository.saveAll(expectedCompetitions);

        List<Competition> actualCompetitions = competitionRepository.findAll();

        assertEquals(expectedCompetitions, actualCompetitions);
    }

    @Test
    public void testDelete() {
        Competition competition = getCompetitionMock("Color run");
        competition.setSport(sport);
        Long competitionId = competitionRepository.save(competition).getCompetitionId();

        competitionRepository.delete(competition);

        assertThrows(JpaObjectRetrievalFailureException.class, () -> competitionRepository.getById(competitionId));
    }
}
