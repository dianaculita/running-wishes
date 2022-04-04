package com.project.services.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.RegisterUserDto;
import com.project.dtos.auth.TokenDto;
import com.project.models.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    TokenDto registerUser(RegisterUserDto registerUserDto);

    void changePassword(ChangePasswordDto changePasswordDto);
}
