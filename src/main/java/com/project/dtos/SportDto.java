package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SportDto {

    @NotNull
    private Long sportId;

    @NotEmpty
    private String name;
}
