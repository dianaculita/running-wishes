package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompetitionDto {

    private Long competitionId;

    @NotNull
    private String name;

    private Integer numberOfDays;

    @NotNull
    private String location;

    @NotNull
    private Long sportId;

    @NotNull
    private Double ticketFee;

    private Double raisedMoney;

    private List<String> usersCnp;
}
