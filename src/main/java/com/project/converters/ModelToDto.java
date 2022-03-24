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
                .neededFund(charityPerson.getNeededFund())
                .raisedFund(charityPerson.getRaisedFund())
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
                .ticketFee(competition.getTicketFee())
                .raisedMoney(competition.getRaisedMoney())
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

        return SponsorDto.builder()
                .sponsorId(sponsor.getSponsorId())
                .name(sponsor.getName())
                .competitionsIds(competitionsIds)
                .build();
    }

    public static DonationDto donationToDto(Donation donation) {
        return DonationDto.builder()
                .donationId(donation.getDonationId())
                .charityPersonCnp(donation.getCharityPerson().getPersonCnp())
                .competitionId(donation.getCompetition().getCompetitionId())
                .totalFunds(donation.getTotalFunds())
                .build();
    }
}
