package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CharityPersonDto {

    private String personCnp;

    @NonNull
    private String name;

    private Integer age;

    private Character gender;

    private String story;

    @NonNull
    private Long iban;

    @NonNull
    private Long associationId;

    @NonNull
    private Double neededFund;

    @NonNull
    private Double raisedFund;
}
