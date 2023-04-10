package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompetitionDto {

    @NotNull
    private Long competitionId;

    @NotEmpty
    private String name;

    private Integer numberOfDays;

    @NotEmpty
    private String location;

    @NotNull
    private Long sportId;

    @NotNull
    private Double ticketFee;

    private Double raisedMoney;

    private List<String> participantsCnp;

    private List<String> sponsors;
}
