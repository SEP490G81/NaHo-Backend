package org.naho.shared.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.ErrorCode;
import org.naho.shared.exception.ErrorCodeHttpMapper;
import org.naho.shared.exception.ProblemDetailProperties;
import org.naho.shared.logging.ErrorLoggingKeys;
import org.naho.shared.logging.HttpLoggingKeys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorCodeHttpMapper errorCodeHttpMapper;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleApplicationException(
            ApplicationException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCodeHttpMapper.toStatus(errorCode);

        // create problem detail
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        // set default fields
        problemDetail.setType(errorCodeHttpMapper.toType(errorCode));
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setDetail(e.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        // set custom fields
        problemDetail.setProperty(ProblemDetailProperties.ERROR_CODE, errorCode.getCode());
        problemDetail.setProperty(ProblemDetailProperties.TRACE_ID, ThreadContext.get(ProblemDetailProperties.TRACE_ID));
        problemDetail.setProperty(ProblemDetailProperties.TIMESTAMP, Instant.now().toString());

        // logging
        // set http fields
        ThreadContext.put(HttpLoggingKeys.HTTP_STATUS_CODE, String.valueOf(httpStatus));

        // set error fields
        ThreadContext.put(ErrorLoggingKeys.ERROR_CODE, errorCode.getCode());
        ThreadContext.put(ErrorLoggingKeys.ERROR_MESSAGE, e.getMessage());
        ThreadContext.put(ErrorLoggingKeys.ERROR_TYPE, e.getClass().getSimpleName());

        log.warn(errorCode.getTitle(), e);
        return ResponseEntity.status(httpStatus).body(problemDetail);
    }
}

