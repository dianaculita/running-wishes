package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DonationDto {

    private Long donationId;

    @NotNull
    private String charityPersonCnp;

    @NotNull
    private Long competitionId;

    @NotNull
    private Double totalFunds;
}
