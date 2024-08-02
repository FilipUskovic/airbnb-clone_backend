package com.airclone.airbnbclone.user.application.service;

import com.airclone.airbnbclone.infrastructure.config.SecurityUtils;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.exception.UserException;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.FieldsFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class Auth0Service {

    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Value("${okta.oauth2.client-secret}")
    private String clientSecret;

    @Value("${okta.oauth2.issuer}")
    private String domain;

    @Value("${application.auth0.role-landlord-id}")
    private String roleLandlordId;

    public void addLandlordRoleToUser(ReadUserDTO readUserDTO){
        readUserDTO.authorities().stream().noneMatch(role -> role.equals(SecurityUtils.ROLE_LANDLORD));
        try {
            String accessToken = this.getAccessToken();
            assignRoleById(accessToken,readUserDTO.email(), readUserDTO.publicId(), roleLandlordId);
        }catch (Auth0Exception e){
            throw new UserException(e.getMessage());
        }
    }

    private void assignRoleById(String accessToken, String email, UUID publicId, String roleIdToAdd) throws Auth0Exception {
        ManagementAPI mgmt = ManagementAPI.newBuilder(domain, accessToken).build();
        Response<List<User>> auth0userBy = mgmt.users().listByEmail(email, new FieldsFilter()).execute();
        User user = auth0userBy.getBody().stream().findFirst().orElseThrow(() -> new UserException(String.format("User with ublicId %s not found", publicId)));
        mgmt.roles().assignUsers(roleIdToAdd, List.of(user.getId())).execute();
    }

    // getAccess Token form OAUTH0
     public String getAccessToken() throws Auth0Exception {
         AuthAPI auth =  AuthAPI.newBuilder(domain, clientId, clientSecret).build();
         TokenRequest tokenRequest = auth.requestToken(domain + "api/v2/");
         TokenHolder tokenHolder = tokenRequest.execute().getBody();
         return tokenHolder.getAccessToken();
     }


}
