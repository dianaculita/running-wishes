package com.project.controllers;

import com.project.dtos.CharityPersonDto;
import com.project.services.charity.CharityPersonService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charity")
@Tag(name = "Charity Person")
public class CharityPersonController {

    private final CharityPersonService charityPersonService;

    @Autowired
    public CharityPersonController(CharityPersonService charityPersonService) {
        this.charityPersonService = charityPersonService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/{cnp}")
    public CharityPersonDto getCharityPersonByCnp(@PathVariable String cnp) {
        return charityPersonService.getCharityPersonByCnp(cnp);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/getAll")
    public List<CharityPersonDto> getAllCharityPersons() {
        return charityPersonService.getAllCharityPersons();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public String createNewCharityPerson(@RequestBody CharityPersonDto charityPersonDto) {
        return charityPersonService.createNewCharityPerson(charityPersonDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping
    void updateCharityPerson(@RequestBody CharityPersonDto charityPersonDto) {
        charityPersonService.updateCharityPerson(charityPersonDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden Operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping(value = "/{cnp}")
    public void deleteCharityPerson(@PathVariable String cnp) {
        charityPersonService.deleteCharityPerson(cnp);
    }
}
