package com.project.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantDto {

    private String cnp;

    @NonNull
    private String name;

    private Integer age;

    private Character gender;

    private List<Long> competitionsIds;
}
