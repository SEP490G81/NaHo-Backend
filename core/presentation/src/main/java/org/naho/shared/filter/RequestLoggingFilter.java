package org.naho.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.logging.ContextLoggingKey;
import org.naho.logging.HttpLoggingKey;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String REQUEST_COMPLETED_MESSAGE = "Request completed!";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Instant startTime = Instant.now();
        String traceId = resolveTraceId(request);

        ThreadContext.put(ContextLoggingKey.TRACE_ID, traceId);
        ThreadContext.put(ContextLoggingKey.USER_ID, "1234567890");
        ThreadContext.put(ContextLoggingKey.USER_ROLE, "USER");

        ThreadContext.put(HttpLoggingKey.HTTP_METHOD, request.getMethod());
        ThreadContext.put(HttpLoggingKey.HTTP_ROUTE, request.getRequestURI());
        ThreadContext.put(HttpLoggingKey.HTTP_CLIENT_IP, resolveClientIp(request));

        response.setHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();

            ThreadContext.put(HttpLoggingKey.HTTP_STATUS_CODE, String.valueOf(response.getStatus()));
            ThreadContext.put(HttpLoggingKey.HTTP_DURATION_MS, String.valueOf(durationMs));

            log.info(REQUEST_COMPLETED_MESSAGE);

            ThreadContext.clearAll();
        }

    }

    private String resolveTraceId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(TRACE_ID_HEADER))
                .filter(value -> !value.isBlank())
                .orElse(UUID.randomUUID().toString());
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
