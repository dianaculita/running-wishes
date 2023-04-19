package com.project.services.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.RegisterUserDto;
import com.project.dtos.auth.TokenDto;
import com.project.exceptions.UserAlreadyExistsException;
import com.project.exceptions.UserNotFoundException;
import com.project.models.User;
import com.project.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile(value = "!integrationTest")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Registers new user with given username, password and role
     * Role can be "ROLE_ADMIN" or "ROLE_USER"
     * @param registerUserDto contains username, password, role
     * @return token for access token, as login is also performed
     */
    @Override
    public TokenDto registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .username(registerUserDto.getUsername())
                .build();

        Long userId = userRepository.save(user).getId();

        return keycloakAdminService.addUserToKeycloak(userId, registerUserDto.getPassword(), registerUserDto.getRole());
    }

    /**
     * Changes password for a user
     * @param changePasswordDto contains username, new password
     */
    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        if (!userRepository.existsByUsername(changePasswordDto.getUsername())) {
            throw new UserNotFoundException();
        }

        keycloakAdminService.changePassword(changePasswordDto);
    }
}
