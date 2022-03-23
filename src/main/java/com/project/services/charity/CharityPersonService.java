package com.project.services.charity;

import com.project.dtos.CharityPersonDto;

import java.util.List;

public interface CharityPersonService {

    CharityPersonDto getCharityPersonByCnp(String cnp);

    List<CharityPersonDto> getAllCharityPersons();

    String createNewCharityPerson(CharityPersonDto charityPersonDto);

    void updateCharityPerson(CharityPersonDto charityPersonDto);

    void deleteCharityPerson(String cnp);
}
