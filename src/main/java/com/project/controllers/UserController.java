package com.project.controllers;

import com.project.dtos.UserDto;
import com.project.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{cnp}")
    public UserDto getUserById(@PathVariable String cnp) {
        return userService.getUserByCnp(cnp);
    }

    @GetMapping(value = "/getAll")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public String createNewUser(@RequestBody UserDto userDto) {
        return userService.createNewUser(userDto);
    }

    @PutMapping
    public void updateUser(@RequestBody UserDto userDto) {
        userService.updateUser(userDto);
    }

    @DeleteMapping(value = "/{cnp}")
    public void deleteUser(@PathVariable String cnp) {
        userService.deleteUser(cnp);
    }
}
