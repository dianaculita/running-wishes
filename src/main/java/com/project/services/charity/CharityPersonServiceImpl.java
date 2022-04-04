package com.project.services.charity;

import com.project.converters.DtoToModel;
import com.project.converters.ModelToDto;
import com.project.dtos.CharityPersonDto;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.models.Donation;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.DonationRepository;
import com.project.services.association.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharityPersonServiceImpl implements CharityPersonService {

    private final CharityPersonRepository charityPersonRepository;

    private final AssociationService associationService;

    private final DonationRepository donationRepository;

    @Autowired
    public CharityPersonServiceImpl(CharityPersonRepository charityPersonRepository,
                                    AssociationService associationService,
                                    DonationRepository donationRepository) {
        this.charityPersonRepository = charityPersonRepository;
        this.associationService = associationService;
        this.donationRepository = donationRepository;
    }

    @Override
    public CharityPersonDto getCharityPersonByCnp(String cnp) {
        return ModelToDto.charityPersonToDto(getByCnp(cnp));
    }

    private CharityPerson getByCnp(String cnp) {
        return charityPersonRepository.findByPersonCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<CharityPersonDto> getAllCharityPersons() {
        return charityPersonRepository.findAll().stream()
                .map(ModelToDto::charityPersonToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String createNewCharityPerson(CharityPersonDto charityPersonDto) {
        CharityPerson charityPerson = CharityPerson.builder()
                .personCnp(charityPersonDto.getPersonCnp())
                .name(charityPersonDto.getName())
                .story(charityPersonDto.getStory())
                .iban(charityPersonDto.getIban())
                .neededFund(charityPersonDto.getNeededFund())
                .raisedFund(charityPersonDto.getRaisedFund())
                .association(getAssociation(charityPersonDto))
                .build();

        return charityPersonRepository.save(charityPerson).getPersonCnp();
    }

    private Association getAssociation(CharityPersonDto charityPersonDto) {
        return DtoToModel.fromAssociationDto(associationService.getAssociationById(charityPersonDto.getAssociationId()));
    }

    /**
     * Once a charity person is enrolled to an association, he/she can not move to another association
     */
    @Override
    public void updateCharityPerson(CharityPersonDto charityPersonDto) {
        CharityPerson charityPerson = getByCnp(charityPersonDto.getPersonCnp());

        charityPerson.setName(charityPersonDto.getName());
        charityPerson.setIban(charityPersonDto.getIban());
        charityPerson.setStory(charityPersonDto.getStory());
        charityPerson.setRaisedFund(charityPersonDto.getRaisedFund());
        charityPerson.setNeededFund(charityPersonDto.getNeededFund());

        charityPersonRepository.save(charityPerson);
    }

    @Override
    public void deleteCharityPerson(String cnp) {
        CharityPerson charityPerson = getByCnp(cnp);
        List<Donation> donations = charityPerson.getDonations();
        donationRepository.deleteAll(donations);

        charityPersonRepository.delete(charityPerson);
    }

}
