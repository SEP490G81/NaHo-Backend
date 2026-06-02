package org.naho.config.security;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.naho.user.constant.JwtProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] API_WHITELIST = {
            "/api/v1/auth/login",
            "/api/v1/users/**",
            "/api/v1/roles/**"
    };

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(API_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtProperty jwtProperty) {
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8)
        );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
