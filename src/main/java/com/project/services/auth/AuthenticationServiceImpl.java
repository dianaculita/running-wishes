package com.project.services.auth;

import com.project.clients.AuthenticationClient;
import com.project.dtos.auth.LoginDto;
import com.project.dtos.auth.RefreshTokenDto;
import com.project.dtos.auth.TokenDto;
import com.project.exceptions.UserNotFoundException;
import com.project.models.User;
import com.project.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Service
@Profile(value = "!integrationTest")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationClient authenticationClient;

    private final UserRepository userRepository;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationClient authenticationClient,
                                     UserRepository userRepository) {
        this.authenticationClient = authenticationClient;
        this.userRepository = userRepository;
    }

    /**
     * Performs login for a user with given username, password and grantType being "password"
     * @param loginDto contains username, password, grantType
     * @return a token that contains the user authentication access token
     */
    public TokenDto login(LoginDto loginDto) {
        Optional<User> inAppUser = userRepository.findByUsername(loginDto.getUsername());

        if (!inAppUser.isPresent()) {
            throw new UserNotFoundException();
        }

        MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();

        loginCredentials.add("client_id", keycloakClient);
        loginCredentials.add("username", inAppUser.get().getId().toString());
        loginCredentials.add("password", loginDto.getPassword());
        loginCredentials.add("grant_type", loginDto.getGrantType());

        return authenticationClient.login(loginCredentials);
    }

    /**
     * Requests a new access token based on the refresh token (must not have been expired),
     * if the old access token has expired
     * @param refreshTokenDto contains the refresh token and grantType being "grant_type"
     * @return a token that contains new authentication access token
     */
    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {
        MultiValueMap<String, String> refreshCredentials = new LinkedMultiValueMap<>();

        refreshCredentials.add("client_id", keycloakClient);
        refreshCredentials.add("refresh_token", refreshTokenDto.getRefreshToken());
        refreshCredentials.add("grant_type", refreshTokenDto.getGrantType());

        return authenticationClient.refresh(refreshCredentials);
    }
}
