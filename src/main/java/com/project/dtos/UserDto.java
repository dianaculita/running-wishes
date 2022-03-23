package com.project.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private String cnp;

    private String name;

    private Integer age;

    private Character gender;

    private List<Long> competitionsIds;
}
