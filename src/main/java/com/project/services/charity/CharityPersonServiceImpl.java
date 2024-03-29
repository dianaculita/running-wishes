package com.project.services.charity;

import com.project.dtos.CharityPersonDto;
import com.project.mappers.CharityPersonMapper;
import com.project.models.Association;
import com.project.models.CharityPerson;
import com.project.models.Donation;
import com.project.repositories.AssociationRepository;
import com.project.repositories.CharityPersonRepository;
import com.project.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharityPersonServiceImpl implements CharityPersonService {

    private final CharityPersonRepository charityPersonRepository;

    private final AssociationRepository associationRepository;

    private final DonationRepository donationRepository;

    private final CharityPersonMapper charityPersonMapper;

    @Autowired
    public CharityPersonServiceImpl(CharityPersonRepository charityPersonRepository,
                                    AssociationRepository associationRepository,
                                    DonationRepository donationRepository,
                                    CharityPersonMapper charityPersonMapper) {
        this.charityPersonRepository = charityPersonRepository;
        this.associationRepository = associationRepository;
        this.donationRepository = donationRepository;
        this.charityPersonMapper = charityPersonMapper;
    }

    @Override
    public CharityPersonDto getCharityPersonByCnp(String cnp) {
        return charityPersonMapper.personEntityToDto(getByCnp(cnp));
    }

    private CharityPerson getByCnp(String cnp) {
        return charityPersonRepository.findByPersonCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<CharityPersonDto> getAllCharityPersons() {
        return charityPersonRepository.findAll().stream()
                .map(charityPersonMapper::personEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String createNewCharityPerson(CharityPersonDto charityPersonDto) {
        Association association = getAssociation(charityPersonDto);
        CharityPerson charityPerson = CharityPerson.builder()
                .personCnp(charityPersonDto.getPersonCnp())
                .name(charityPersonDto.getName())
                .story(charityPersonDto.getStory())
                .iban(charityPersonDto.getIban())
                .neededFund(charityPersonDto.getNeededFund())
                .raisedFund(charityPersonDto.getRaisedFund())
                .association(association)
                .build();
        charityPerson = charityPersonRepository.save(charityPerson);


        association.getCharityPeople().add(charityPerson);
        associationRepository.save(association);

        return charityPerson.getPersonCnp();
    }

    private Association getAssociation(CharityPersonDto charityPersonDto) {
        return associationRepository.findById(charityPersonDto.getAssociationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        if (donations.size() > 0) {
            donationRepository.deleteAll(donations);
        }
        charityPersonRepository.delete(charityPerson);
    }

}
