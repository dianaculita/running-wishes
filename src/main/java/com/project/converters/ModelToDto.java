package com.project.converters;

import com.project.dtos.*;
import com.project.models.*;

import java.util.List;
import java.util.stream.Collectors;

public class ModelToDto {

    public static AssociationDto associationToDto(Association association) {
        return AssociationDto.builder()
                .associationId(association.getAssociationId())
                .name(association.getName())
                .purpose(association.getPurpose())
                .build();
    }

    public static CharityPersonDto charityPersonToDto(CharityPerson charityPerson) {
        return CharityPersonDto.builder()
                .personCnp(charityPerson.getPersonCnp())
                .name(charityPerson.getName())
                .age(charityPerson.getAge())
                .gender(charityPerson.getGender())
                .iban(charityPerson.getIban())
                .story(charityPerson.getStory())
                .associationId(charityPerson.getAssociation().getAssociationId())
                .build();
    }

    public static SportDto sportToDto(Sport sport) {
        return SportDto.builder()
                .sportId(sport.getSportId())
                .name(sport.getName())
                .build();
    }

    public static CompetitionDto competitionToDto(Competition competition) {
        List<String> participants = competition.getUsers().stream()
                .map(User::getCnp)
                .collect(Collectors.toList());

        return CompetitionDto.builder()
                .competitionId(competition.getCompetitionId())
                .name(competition.getName())
                .location(competition.getLocation())
                .numberOfDays(competition.getNumberOfDays())
                .sportId(competition.getSport().getSportId())
                .usersCnp(participants)
                .build();
    }

    public static UserDto userToDto(User user) {
        List<Long> competitionsIds = user.getParticipatesToCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        return UserDto.builder()
                .cnp(user.getCnp())
                .name(user.getName())
                .age(user.getAge())
                .gender(user.getGender())
                .competitionsIds(competitionsIds)
                .build();
    }

    public static SponsorDto sponsorToDto(Sponsor sponsor) {
        List<Long> competitionsIds = sponsor.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        List<Long> donationsIds = sponsor.getDonations().stream()
                .map(Donation::getDonationId)
                .collect(Collectors.toList());

        return SponsorDto.builder()
                .sponsorId(sponsor.getSponsorId())
                .name(sponsor.getName())
                .competitionsIds(competitionsIds)
                .donationsIds(donationsIds)
                .build();
    }

    public static DonationDto donationToDto(Donation donation) {
        return DonationDto.builder()
                .donationId(donation.getDonationId())
                .charityPersonCnp(donation.getCharityPerson().getPersonCnp())
                .sponsorId(donation.getSponsor().getSponsorId())
                .totalFunds(donation.getTotalFunds())
                .build();
    }
}
