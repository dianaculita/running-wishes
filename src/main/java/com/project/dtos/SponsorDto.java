package com.project.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SponsorDto {

    private Long sponsorId;

    private String name;

    private Double sponsoringFunds;

    private List<Long> competitionsIds;

}
