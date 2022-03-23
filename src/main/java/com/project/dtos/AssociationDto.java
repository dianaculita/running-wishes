package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssociationDto {

    private Long associationId;

    private String name;

    private String purpose;
}
