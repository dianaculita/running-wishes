package com.project.services.auth;

import com.project.dtos.auth.TokenDto;

public interface KeycloakAdminService {

    TokenDto addUserToKeycloak(Long userCnp, String password, String role);

}
