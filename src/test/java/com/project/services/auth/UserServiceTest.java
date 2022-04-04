package com.project.services.auth;

import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.RegisterUserDto;
import com.project.dtos.auth.TokenDto;
import com.project.exceptions.UserAlreadyExistsException;
import com.project.exceptions.UserNotFoundException;
import com.project.models.User;
import com.project.repositories.auth.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.ws.rs.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakAdminService keycloakAdminService;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(userRepository, keycloakAdminService);
    }

    @Test
    public void testRegisterUser() {
        String username = "username";
        String password = "pass";
        RegisterUserDto registerUserDto = new RegisterUserDto(username, password, "ROLE_USER");
        User user = User.builder()
                .username(username)
                .id(100L)
                .build();

        TokenDto expectedToken = new TokenDto();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(keycloakAdminService.addUserToKeycloak(100L, password, "ROLE_USER")).thenReturn(expectedToken);

        TokenDto actualToken = userService.registerUser(registerUserDto);

        assertEquals(expectedToken, actualToken);

        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).save(any(User.class));
        verify(keycloakAdminService).addUserToKeycloak(anyLong(), anyString(), anyString());
    }

    @Test
    public void testRegisterUser_shouldThrowUserAlreadyExistsException() {
        String username = "username";
        String password = "pass";
        RegisterUserDto registerUserDto = new RegisterUserDto("username", password, "ROLE_USER");

        doThrow(new UserAlreadyExistsException()).when(userRepository).existsByUsername(username);

        BadRequestException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registerUserDto));
        assertTrue(exception.getMessage().contains("The user with this username already exists!"));

        verify(userRepository).existsByUsername(anyString());
    }

    @Test
    public void testChangePassword() {
        String username = "username";
        String password = "pass";

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(username, password);

        when(userRepository.existsByUsername(username)).thenReturn(true);
        doNothing().when(keycloakAdminService).changePassword(changePasswordDto);

        userService.changePassword(changePasswordDto);

        verify(userRepository).existsByUsername(anyString());
        verify(keycloakAdminService).changePassword(any(ChangePasswordDto.class));
    }

    @Test
    public void testChangePassword_shouldThrowUserNotFoundException() {
        String username = "username";
        String password = "pass";

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(username, password);

        doThrow(new UserNotFoundException()).when(userRepository).existsByUsername(username);

        BadRequestException exception = assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(changePasswordDto));
        assertTrue(exception.getMessage().contains("This user does not exist!"));

        verify(userRepository).existsByUsername(anyString());
    }
}
