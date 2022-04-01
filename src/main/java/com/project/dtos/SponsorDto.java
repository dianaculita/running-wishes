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
public class SponsorDto {

    @NotNull
    private Long sponsorId;

    @NotEmpty
    private String name;

    @NotNull
    private Double sponsoringFunds;

    @NotNull
    private List<Long> competitionsIds;

}
