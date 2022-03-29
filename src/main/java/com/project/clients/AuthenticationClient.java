package com.project.clients;

import com.project.configuration.AuthenticationClientConfiguration;
import com.project.dtos.auth.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "auth", configuration = AuthenticationClientConfiguration.class,
                url = "${my.keycloak.url}")
public interface AuthenticationClient {

    @PostMapping(value = "${my.keycloak.auth.endpoint}")
    TokenDto login(@RequestBody Map<String, ?> login);

    @PostMapping(value = "${my.keycloak.auth.endpoint}")
    TokenDto refresh(Map<String, ?> refresh);

}
