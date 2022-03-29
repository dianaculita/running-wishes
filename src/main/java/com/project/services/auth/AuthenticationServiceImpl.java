package com.project.services.auth;

import com.project.clients.AuthenticationClient;
import com.project.dtos.auth.LoginDto;
import com.project.dtos.auth.RefreshTokenDto;
import com.project.dtos.auth.TokenDto;
import com.project.models.User;
import com.project.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

@Service
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

    public TokenDto login(LoginDto loginDto) {
        Optional<User> inAppUser = userRepository.findByUsername(loginDto.getUsername());

        if (!inAppUser.isPresent()) {
            throw new NotFoundException("The user doesn't exist!");
        }

        MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();

        loginCredentials.add("client_id", keycloakClient);
        loginCredentials.add("username", inAppUser.get().getId().toString());
        loginCredentials.add("password", loginDto.getPassword());
        loginCredentials.add("grant_type", loginDto.getGrantType());

        return authenticationClient.login(loginCredentials);
    }

    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {
        MultiValueMap<String, String> refreshCredentials = new LinkedMultiValueMap<>();

        refreshCredentials.add("client_id", keycloakClient);
        refreshCredentials.add("refresh_token", refreshTokenDto.getRefreshToken());
        refreshCredentials.add("grant_type", refreshTokenDto.getGrantType());

        return authenticationClient.refresh(refreshCredentials);
    }
}
