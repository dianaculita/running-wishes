package com.project.converters;

import com.project.dtos.*;
import com.project.models.*;

public class DtoToModel {

    public static Association fromAssociationDto(AssociationDto associationDto) {
        return Association.builder()
                .name(associationDto.getName())
                .purpose(associationDto.getPurpose())
                .build();
    }

    public static CharityPerson fromCharityPersonDto(CharityPersonDto charityPersonDto) {
        return CharityPerson.builder()
                .personCnp(charityPersonDto.getPersonCnp())
                .age(charityPersonDto.getAge())
                .gender(charityPersonDto.getGender())
                .iban(charityPersonDto.getIban())
                .story(charityPersonDto.getStory())
                .build();
    }

    public static Sponsor fromSponsorDto(SponsorDto sponsorDto) {
        return Sponsor.builder()
                .sponsorId(sponsorDto.getSponsorId())
                .name(sponsorDto.getName())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {
        return User.builder()
                .cnp(userDto.getCnp())
                .name(userDto.getName())
                .age(userDto.getAge())
                .gender(userDto.getGender())
                .build();
    }

    public static Sport fromSportDto(SportDto sportDto) {
        return Sport.builder()
                .sportId(sportDto.getSportId())
                .name(sportDto.getName())
                .build();
    }

    public static Competition fromCompetitionDto(CompetitionDto competitionDto) {
        return Competition.builder()
                .competitionId(competitionDto.getCompetitionId())
                .name(competitionDto.getName())
                .location(competitionDto.getLocation())
                .numberOfDays(competitionDto.getNumberOfDays())
                .build();
    }

    public static Donation fromDonationDto(DonationDto donationDto) {
        return Donation.builder()
                .donationId(donationDto.getDonationId())
                .totalFunds(donationDto.getTotalFunds())
                .build();
    }



}
