package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SportDto {

    private Long sportId;

    @NotNull
    private String name;
}
