package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CharityPersonDto {

    private String personCnp;

    private String name;

    private Integer age;

    private Character gender;

    private String story;

    private Long iban;

    private Long associationId;
}
