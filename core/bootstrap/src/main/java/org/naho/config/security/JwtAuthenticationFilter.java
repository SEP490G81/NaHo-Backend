package org.naho.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.naho.user.port.out.PermissionRepositoryPort;
import org.naho.user.port.out.TokenServicePort;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenServicePort tokenServicePort;
    private final PermissionRepositoryPort permissionRepositoryPort;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = resolveBearerToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication existingAuthentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (existingAuthentication != null && existingAuthentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        AccessTokenPayload payload = tokenServicePort.verifyAccessToken(accessToken);

        List<String> permissions = permissionRepositoryPort.findAllPermissionCodeByUserId(payload.userId());
        permissions.add("USER_REGISTER");

        List<SimpleGrantedAuthority> authorities = permissions
                .stream().map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                payload,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization == null || authorization.isBlank()) {
            return null;
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();

        return token.isBlank() ? null : token;
    }
}
