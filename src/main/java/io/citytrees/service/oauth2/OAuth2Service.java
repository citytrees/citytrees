package io.citytrees.service.oauth2;

import javax.servlet.http.HttpServletResponse;

public interface OAuth2Service {

    String getOAuthUri();

    void handleOAuthFlow(String authorizationCode, HttpServletResponse response);
}
