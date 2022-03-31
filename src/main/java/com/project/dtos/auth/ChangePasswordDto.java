package com.project.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

    @NonNull
    private String username;

    @NonNull
    private String newPassword;

}
