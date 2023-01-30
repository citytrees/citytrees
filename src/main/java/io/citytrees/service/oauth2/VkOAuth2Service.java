package io.citytrees.service.oauth2;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import io.citytrees.configuration.properties.ApplicationProperties;
import io.citytrees.configuration.security.OAuth2Config;
import io.citytrees.model.User;
import io.citytrees.service.AuthService;
import io.citytrees.service.UserService;
import io.citytrees.v1.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VkOAuth2Service implements OAuth2Service {

    private static final String PROVIDER_ID = "vk";

    private final UserService userService;
    private final AuthService authService;
    private final OAuth2Config oAuth2Config;
    private final ApplicationProperties applicationProperties;
    private final VkApiClient vk;

    @Override
    public String getOAuthUri() {
        return new URIBuilder()
                .setHost("https://oauth.vk.com")
                .setPath("authorize")
                .setParameter("client_id", oAuth2Config.getClientId(PROVIDER_ID))
                .setParameter("display", "page")
                .setParameter("redirect_uri", applicationProperties.getBaseUrl() + "/auth/oauth2/vk/callback")
                .setParameter("scope", "groups")
                .setParameter("response_type", "code")
                .toString();
    }

    @Override
    public void handleOAuthFlow(String authorizationCode, HttpServletResponse response) {

        UserAuthResponse authResponse;
        try {
            authResponse = vk.oAuth().userAuthorizationCodeFlow(
                    Integer.parseInt(oAuth2Config.getClientId(PROVIDER_ID)),
                    oAuth2Config.getClientSecret(PROVIDER_ID),
                    getRedirectUri(),
                    authorizationCode
            ).execute();
        } catch (ApiException | ClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }

        User user = userService.findByAuthProviderIdAndExternalUserId(PROVIDER_ID, Long.valueOf(authResponse.getUserId()));
        if (user == null) {
            user = createNewUser(authResponse.getUserId(), authResponse.getAccessToken());
        }

        authService.generateAndSetAuthCookies(user, response);
    }

    private String getRedirectUri() {
        return new URIBuilder()
                .setHost(applicationProperties.getBaseUrl())
                .setPath("/auth/oauth2/vk/callback")
                .toString();
    }

    private User createNewUser(Integer userVkId, String token) {
        try {
            UserActor actor = new UserActor(userVkId, token);
            List<GetResponse> getUsersResponse = vk.users().get(actor).userIds(String.valueOf(userVkId)).execute();
            GetResponse userR = getUsersResponse.get(0);

            User user = User.builder()
                    .firstName(userR.getFirstName())
                    .lastName(userR.getLastName())
                    .roles(Set.of(UserRole.BASIC))
                    .email(userR.getEmail())
                    .authProviderMeta(List.of(
                        User.AuthProviderMeta.builder()
                                .id(PROVIDER_ID)
                                .params(Map.of(
                                    "id", userR.getId()
                                ))
                                .build()
                    ))
                    .build();

            userService.create(user);

            return user;

        } catch (ApiException | ClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }
    }
}
