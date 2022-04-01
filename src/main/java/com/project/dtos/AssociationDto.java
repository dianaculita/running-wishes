package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssociationDto {

    @NotNull
    private Long associationId;

    @NotEmpty
    private String name;

    private String purpose;
}
