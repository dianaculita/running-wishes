package com.project.controllers.auth;

import com.project.dtos.auth.LoginDto;
import com.project.dtos.auth.RefreshTokenDto;
import com.project.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(LoginDto loginDto) {
        return new ResponseEntity<>(authenticationService.login(loginDto), HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(RefreshTokenDto refreshTokenDto) {
        return new ResponseEntity<>(authenticationService.refresh(refreshTokenDto), HttpStatus.OK);
    }
}
