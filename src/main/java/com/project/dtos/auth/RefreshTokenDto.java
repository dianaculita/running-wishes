package com.project.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    @NotEmpty
    private String refreshToken;

    @NotEmpty
    private String grantType;

}
