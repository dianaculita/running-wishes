package com.project.services.sport;

import com.project.converters.ModelToDto;
import com.project.dtos.SportDto;
import com.project.models.Sport;
import com.project.repositories.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;

    @Autowired
    public SportServiceImpl(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @Override
    public SportDto getSportById(Long id) {
        return ModelToDto.sportToDto(getById(id));
    }

    private Sport getById(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<SportDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(ModelToDto::sportToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long createNewSport(SportDto sportDto) {
        Sport sport = Sport.builder()
                .name(sportDto.getName())
                .build();

        return sportRepository.save(sport).getSportId();
    }

    @Override
    public void updateSport(SportDto sportDto) {
        Sport sport = getById(sportDto.getSportId());

        sport.setName(sportDto.getName());

        sportRepository.save(sport);
    }

    @Override
    public void deleteSport(Long id) {
        sportRepository.delete(getById(id));
    }

}
