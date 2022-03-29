package com.project.services.auth;

import com.project.dtos.auth.LoginDto;
import com.project.dtos.auth.RefreshTokenDto;
import com.project.dtos.auth.TokenDto;

public interface AuthenticationService {

    TokenDto login(LoginDto loginDto);

    TokenDto refresh(RefreshTokenDto refreshTokenDto);

}
