package com.project.services.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.RegisterUserDto;
import com.project.dtos.auth.TokenDto;

public interface UserService {

    TokenDto registerUser(RegisterUserDto registerUserDto);

    void changePassword(ChangePasswordDto changePasswordDto);
}
