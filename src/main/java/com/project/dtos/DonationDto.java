package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DonationDto {

    private Long donationId;

    @NonNull
    private String charityPersonCnp;

    @NonNull
    private Long competitionId;

    @NonNull
    private Double totalFunds;
}
