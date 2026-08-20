package org.naho.shared.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.helper.CookieFactory;
import org.naho.user.helper.LoginRequestResolver;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.result.LoginResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final LoginRequestResolver loginRequestResolver;
    private final AuthInputPort authInputPort;
    private final CookieFactory cookieFactory;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String sub = oAuth2User.getAttribute("sub");
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            String deviceId = loginRequestResolver.getDeviceId(request);
            if (deviceId == null || deviceId.isBlank()) {
                deviceId = "google-oauth2-" + sub;
            }

            GoogleLoginCommand command = GoogleLoginCommand.builder()
                    .sub(sub)
                    .email(email)
                    .fullName(name)
                    .pictureUrl(picture)
                    .deviceId(deviceId)
                    .userAgent(loginRequestResolver.getUserAgent(request))
                    .ipAddress(loginRequestResolver.getIpAddress(request))
                    .build();

            LoginResult result = authInputPort.googleLogin(command);

            ResponseCookie accessTokenCookie =
                    cookieFactory.createCookieForJWTToken(result.accessToken());
            ResponseCookie refreshTokenCookie =
                    cookieFactory.createCookieForJWTToken(result.refreshToken());

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            response.sendRedirect(frontendUrl);
        } catch (Exception e) {
            log.error("Google OAuth2 login failed: ", e);
            response.sendRedirect(frontendUrl + "/login?error=oauth2_failed");
        }
    }
}
