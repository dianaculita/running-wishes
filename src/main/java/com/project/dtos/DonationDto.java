package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DonationDto {

    @NotNull
    private Long donationId;

    @NotEmpty
    private String charityPersonCnp;

    @NotNull
    private Long competitionId;

    @NotNull
    private Double totalFunds;
}
