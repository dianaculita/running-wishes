package com.project.services.sport;

import com.project.dtos.SportDto;

import java.util.List;

public interface SportService {

    SportDto getSportById(Long id);

    List<SportDto> getAllSports();

    Long createNewSport(SportDto sportDto);

    void deleteSport(Long id);
}
