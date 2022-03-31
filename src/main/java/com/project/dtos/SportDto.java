package com.project.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SportDto {

    private Long sportId;

    @NonNull
    private String name;
}
