package com.project.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompetitionDto {

    private Long competitionId;

    private String name;

    private Integer numberOfDays;

    private String location;

    private Long sportId;

    private Double ticketFee;

    private Double raisedMoney;

    private List<String> usersCnp;
}
