package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssociationDto {

    private Long associationId;

    @NonNull
    private String name;

    private String purpose;
}
