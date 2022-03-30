package com.project.controllers.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.RegisterUserDto;
import com.project.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registerUser")
    public void registerUser(@RequestBody RegisterUserDto registerUserDto) {
        userService.registerUser(registerUserDto);
    }

    @PutMapping("/changePassword")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
    }
}
