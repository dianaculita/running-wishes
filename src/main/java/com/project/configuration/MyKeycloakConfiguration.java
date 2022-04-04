package com.project.configuration;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * KeycloakWebSecurityConfigurerAdapter: base class for creating a WebSecurityConfigurer instance
 * Provides the security context configuration
 */
@KeycloakConfiguration
@Import(KeycloakSpringBootConfigResolver.class)
public class MyKeycloakConfiguration extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {

        KeycloakAuthenticationProvider keycloakAuthenticationProvider
                = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                new SimpleAuthorityMapper());
        authenticationManagerBuilder.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

    /**
     * Defining security constraints
     * Automatically authorizing requests for user registration, login and refresh
     * User participants will have access only on participant creation/update and on any getAll method
     * Admin will have access on every other application logic (competitions, donations etc)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/*", "/login", "/refresh", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .antMatchers("/participant", "/participant/getAll", "/competition/getAll",
                        "/sport/getAll", "/sponsor/getAll", "charity/getAll",
                        "/association/getAll").hasRole("USER")
                .antMatchers("/competition/**", "/sport/**", "/sponsor/**", "/donation/**",
                        "/charity/**", "/association/**", "/participant/**").hasRole("ADMIN")
                .anyRequest().authenticated();
    }
}
