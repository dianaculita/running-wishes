package com.project.services.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.TokenDto;

public interface KeycloakAdminService {

    TokenDto addUserToKeycloak(Long userCnp, String password, String role);

    void changePassword(ChangePasswordDto changePasswordDto);

}
