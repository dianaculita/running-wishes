package com.project.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String role;
}
