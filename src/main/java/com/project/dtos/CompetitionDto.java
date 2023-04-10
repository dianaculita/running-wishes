package com.project.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompetitionDto {

    @NotNull
    private Long competitionId;

    @NotEmpty
    private String name;

    private Integer numberOfDays;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @NotEmpty
    private Date startDate;

    @NotEmpty
    private String location;

    @NotNull
    private Long sportId;

    @NotNull
    private Double ticketFee;

    private Double raisedMoney;

    private List<String> participantsCnp;

    private List<String> sponsors;
}
