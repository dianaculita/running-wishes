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

    @NonNull
    private String name;

    private Integer numberOfDays;

    @NonNull
    private String location;

    @NonNull
    private Long sportId;

    @NonNull
    private Double ticketFee;

    private Double raisedMoney;

    private List<String> usersCnp;
}
