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

    private List<Long> competitionsIds;

    private List<Long> donationsIds;

}
