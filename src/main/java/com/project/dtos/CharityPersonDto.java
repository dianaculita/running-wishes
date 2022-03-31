package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CharityPersonDto {

    private String personCnp;

    @NotNull
    private String name;

    private Integer age;

    private Character gender;

    private String story;

    @NotNull
    private Long iban;

    @NotNull
    private Long associationId;

    @NotNull
    private Double neededFund;

    @NotNull
    private Double raisedFund;
}
