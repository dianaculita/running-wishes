package com.project.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String grantType;
}
