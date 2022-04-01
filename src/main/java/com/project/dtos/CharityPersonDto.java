package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CharityPersonDto {

    @NotEmpty
    private String personCnp;

    @NotEmpty
    private String name;

    private String story;

    @NotEmpty
    private Long iban;

    @NotNull
    private Long associationId;

    @NotNull
    private Double neededFund;

    @NotNull
    private Double raisedFund;
}
