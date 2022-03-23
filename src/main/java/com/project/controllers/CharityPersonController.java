package com.project.controllers;

import com.project.dtos.CharityPersonDto;
import com.project.services.charity.CharityPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charity")
public class CharityPersonController {

    private final CharityPersonService charityPersonService;

    @Autowired
    public CharityPersonController(CharityPersonService charityPersonService) {
        this.charityPersonService = charityPersonService;
    }

    @GetMapping(value = "/{cnp}")
    public CharityPersonDto getCharityPersonByCnp(@PathVariable String cnp) {
        return charityPersonService.getCharityPersonByCnp(cnp);
    }

    @GetMapping(value = "/getAll")
    public List<CharityPersonDto> getAllCharityPersons() {
        return charityPersonService.getAllCharityPersons();
    }

    @PostMapping
    public String createNewCharityPerson(@RequestBody CharityPersonDto charityPersonDto) {
        return charityPersonService.createNewCharityPerson(charityPersonDto);
    }

    @PutMapping
    void updateCharityPerson(@RequestBody CharityPersonDto charityPersonDto) {
        charityPersonService.updateCharityPerson(charityPersonDto);
    }

    @DeleteMapping(value = "/{cnp}")
    public void deleteCharityPerson(@PathVariable String cnp) {
        charityPersonService.deleteCharityPerson(cnp);
    }
}
