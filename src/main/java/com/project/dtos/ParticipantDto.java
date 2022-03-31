package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantDto {

    private String cnp;

    @NotNull
    private String name;

    private Integer age;

    private Character gender;

    private List<Long> competitionsIds;
}
