package org.naho.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.i18n.MessageService;
import org.naho.logging.ErrorLoggingKey;
import org.naho.logging.HttpLoggingKey;
import org.naho.shared.constant.ProblemDetailProperty;
import org.naho.shared.exception.ErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        ErrorCode errorCode = UserErrorCode.USER_UNAUTHORIZED;

        // create problem detail
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        // set default fields
        problemDetail.setType(URI.create("https://naho.org/problem/authentication-error"));
        problemDetail.setTitle(messageService.getMessage(errorCode.getTitleKey()));
        problemDetail.setDetail(messageService.getMessage(authException.getMessage()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        // set custom fields
        problemDetail.setProperty(ProblemDetailProperty.ERROR_CODE, errorCode.getCode());
        problemDetail.setProperty(ProblemDetailProperty.TRACE_ID, ThreadContext.get(ProblemDetailProperty.TRACE_ID));
        problemDetail.setProperty(ProblemDetailProperty.TIMESTAMP, Instant.now().toString());
        ThreadContext.put(HttpLoggingKey.HTTP_STATUS_CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        ThreadContext.put(ErrorLoggingKey.ERROR_CODE, errorCode.getCode());
        ThreadContext.put(ErrorLoggingKey.ERROR_MESSAGE, authException.getMessage());
        ThreadContext.put(ErrorLoggingKey.ERROR_TYPE, authException.getClass().getSimpleName());

        log.warn(messageService.getMessage(errorCode.getTitleKey()), authException);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }
}
