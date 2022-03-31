package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssociationDto {

    private Long associationId;

    @NotNull
    private String name;

    private String purpose;
}
