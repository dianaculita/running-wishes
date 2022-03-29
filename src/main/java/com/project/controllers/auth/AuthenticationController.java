package com.project.controllers.auth;

import com.project.dtos.auth.LoginDto;
import com.project.dtos.auth.RefreshTokenDto;
import com.project.services.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginDto loginDto) {
        return new ResponseEntity<>(authenticationService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(RefreshTokenDto refreshTokenDto) {
        return new ResponseEntity<>(authenticationService.refresh(refreshTokenDto), HttpStatus.OK);
    }
}
