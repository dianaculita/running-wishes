package com.project.converters;

import com.project.dtos.AssociationDto;
import com.project.dtos.SportDto;
import com.project.models.Association;
import com.project.models.Sport;

/**
 * Util class for converting a dto object to an entity model object
 */
public class DtoToModel {

    public static Association fromAssociationDto(AssociationDto associationDto) {
        return Association.builder()
                .name(associationDto.getName())
                .purpose(associationDto.getPurpose())
                .build();
    }

    public static Sport fromSportDto(SportDto sportDto) {
        return Sport.builder()
                .sportId(sportDto.getSportId())
                .name(sportDto.getName())
                .build();
    }

}
