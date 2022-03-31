package com.project.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    @NonNull
    private String refreshToken;

    @NonNull
    private String grantType;

}
