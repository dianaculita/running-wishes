package com.project.services.user;

import com.project.dtos.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserByCnp(String cnp);

    List<UserDto> getAllUsers();

    String createNewUser(UserDto userDto);

    void updateUser(UserDto userDto);

    void deleteUser(String cnp);
}
