package com.project.converters;

import com.project.dtos.*;
import com.project.models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for converting an entity model object to a dto object
 */
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
        List<String> participants = competition.getParticipants().stream()
                .map(Participant::getCnp)
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

    public static ParticipantDto participantToDto(Participant participant) {
        List<Long> competitionsIds = participant.getCompetitions().stream()
                .map(Competition::getCompetitionId)
                .collect(Collectors.toList());

        return ParticipantDto.builder()
                .cnp(participant.getCnp())
                .name(participant.getName())
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
                .sponsoringFunds(sponsor.getSponsoringFunds())
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
