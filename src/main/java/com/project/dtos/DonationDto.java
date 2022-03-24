package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DonationDto {

    private Long donationId;

    private String charityPersonCnp;

    private Long competitionId;

    private Double totalFunds;
}
