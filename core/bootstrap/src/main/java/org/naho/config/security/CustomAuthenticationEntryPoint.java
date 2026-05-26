package org.naho.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.i18n.MessageService;
import org.naho.shared.constant.ErrorLoggingKey;
import org.naho.shared.constant.HttpLoggingKey;
import org.naho.shared.exception.ErrorCode;
import org.naho.shared.exception.ProblemDetailHelper;
import org.naho.user.exception.UserApplicationErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final ProblemDetailHelper problemDetailHelper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = UserApplicationErrorCode.USER_UNAUTHORIZED;

        ProblemDetail problemDetail = problemDetailHelper.buildProblemDetail(errorCode, authException, request);

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
