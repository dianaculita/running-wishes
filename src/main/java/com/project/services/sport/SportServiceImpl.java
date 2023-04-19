package com.project.services.sport;

import java.util.List;
import java.util.stream.Collectors;

import com.project.dtos.SportDto;
import com.project.mappers.SportMapper;
import com.project.models.Sport;
import com.project.repositories.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final SportMapper sportMapper;

    @Autowired
    public SportServiceImpl(SportRepository sportRepository, SportMapper sportMapper) {
        this.sportRepository = sportRepository;
        this.sportMapper = sportMapper;
    }

    @Override
    public SportDto getSportById(Long id) {
        return sportMapper.sportEntityToDto(getById(id));
    }

    private Sport getById(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<SportDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(sportMapper::sportEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long createNewSport(SportDto sportDto) {
        Sport sport = Sport.builder()
                .name(sportDto.getName())
                .build();

        return sportRepository.save(sport).getSportId();
    }

}
