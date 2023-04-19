package com.project.dtos;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private LocalDate startDate;

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
