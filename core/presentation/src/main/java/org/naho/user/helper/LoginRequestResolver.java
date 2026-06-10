package org.naho.user.helper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

@Component
@RequiredArgsConstructor
public class LoginRequestResolver {
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private final Parser parser;

    public String getIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_HEADER);
    }

    public String getDeviceName(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        Client client = parser.parse(userAgent);

        String browser = client.userAgent.family;
        String os = client.os.family;
        return browser + " on " + os;
    }
}
