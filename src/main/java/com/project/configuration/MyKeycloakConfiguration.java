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

/*
    KeycloakWebSecurityConfigurerAdapter: base class for creating a WebSecurityConfigurer instance
    provides the security context configuration
    @KeycloakConfiguration: defines all annotations that are needed to integrate Keycloak in Spring Security
 */
@KeycloakConfiguration
@Import(KeycloakSpringBootConfigResolver.class)
public class MyKeycloakConfiguration extends KeycloakWebSecurityConfigurerAdapter {

    /*
        registers the KeycloakAuthenticationProvider with the authentication manager
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
     * defining security constraints
     * automatically authorizing requests for user registration, login and refresh
     * user participants will have access only on participant business logic
     * admin will have access on every other application logic (competitions, donations etc)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/register-user", "/login", "/refresh").permitAll()
                .antMatchers("/participant").hasRole("USER")
                .antMatchers("/competition", "/sport", "/sponsor", "/donation",
                        "/charity", "/association").hasRole("ADMIN")
                .anyRequest().authenticated();
    }
}
