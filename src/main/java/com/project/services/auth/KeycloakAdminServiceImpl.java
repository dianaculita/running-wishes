package com.project.services.auth;

import com.project.clients.AuthenticationClient;
import com.project.dtos.auth.ChangePasswordDto;
import com.project.dtos.auth.TokenDto;
import com.project.models.User;
import com.project.repositories.auth.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
@Profile(value = "!integrationTest")
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    private final Keycloak keycloak;

    private final AuthenticationClient authenticationClient;

    private final UserRepository userRepository;

    private RealmResource realmResource;

    @Autowired
    public KeycloakAdminServiceImpl(Keycloak keycloak,
                                    AuthenticationClient authenticationClient,
                                    UserRepository userRepository) {
        this.keycloak = keycloak;
        this.authenticationClient = authenticationClient;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initializeRealmResource() {
        this.realmResource = this.keycloak.realm(keycloakRealm);
    }

    /**
     * Adds a new registered user to the Keycloak realm and also logins the user
     * @return token with access token
     */
    public TokenDto addUserToKeycloak(Long userId, String password, String role) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setEnabled(true);
        newUser.setUsername(userId.toString());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);

        newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        UsersResource usersResource = realmResource.users();

        try (Response response = usersResource.create(newUser)) {

            String createdId = getCreatedId(response);
            usersResource.get(createdId);

            UserResource userResource = realmResource.users().get(createdId);
            RoleRepresentation roleRepresentation = realmResource.roles().get(role).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));

            MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();
            loginCredentials.add("client_id", keycloakClient);
            loginCredentials.add("username", userId.toString());
            loginCredentials.add("password", password);
            loginCredentials.add("grant_type", "password");

            return authenticationClient.login(loginCredentials);
        } catch (WebApplicationException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Changes the password for a given username. Does not perform login
     * @param changePasswordDto contains the user's username and new password
     */
    public void changePassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(changePasswordDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserRepresentation userRepresentation = realmResource.users().search(user.getId().toString()).get(0);

        CredentialRepresentation changedCredential = new CredentialRepresentation();
        changedCredential.setType(CredentialRepresentation.PASSWORD);
        changedCredential.setValue(changePasswordDto.getNewPassword());
        changedCredential.setTemporary(false);

        userRepresentation.setCredentials(Collections.singletonList(changedCredential));

        realmResource.users().get(userRepresentation.getId()).update(userRepresentation);
    }

}
