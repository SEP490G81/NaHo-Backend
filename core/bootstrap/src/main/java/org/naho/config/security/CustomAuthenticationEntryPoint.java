package org.naho.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.MessageService;
import org.naho.shared.exception.ErrorCode;
import org.naho.shared.logging.ErrorLogContextWriter;
import org.naho.shared.response.ProblemDetailResponse;
import org.naho.user.exception.UserErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorMessage = messageService.getMessage(authException.getMessage());

        ProblemDetailResponse problemDetailResponse = ProblemDetailResponse
                .builder(status)
                .type(errorCode.getTypeUri())
                .title(messageService.getMessage(errorCode.getTitleKey()))
                .detail(errorMessage)
                .instance(URI.create(request.getRequestURI()))
                .errorCode(errorCode.getCode())
                .timestamp(Instant.now().toString())
                .build();

        ErrorLogContextWriter.builder()
                .status(status)
                .errorCode(errorCode)
                .exception(authException)
                .errorMessage(errorMessage)
                .write();

        log.warn(messageService.getMessage(errorCode.getTitleKey()), authException);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getOutputStream(), problemDetailResponse);
    }
}
