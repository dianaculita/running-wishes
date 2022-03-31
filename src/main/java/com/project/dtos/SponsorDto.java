package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SponsorDto {

    private Long sponsorId;

    @NotNull
    private String name;

    @NotNull
    private Double sponsoringFunds;

    private List<Long> competitionsIds;

}
