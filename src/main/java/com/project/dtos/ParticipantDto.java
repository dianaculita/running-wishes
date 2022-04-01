package com.project.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantDto {

    @NotEmpty
    private String cnp;

    @NotEmpty
    private String name;

    @NotNull
    private List<Long> competitionsIds;
}
