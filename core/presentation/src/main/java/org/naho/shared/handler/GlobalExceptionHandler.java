package org.naho.shared.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.i18n.MessageService;
import org.naho.shared.constant.ErrorLoggingKey;
import org.naho.shared.constant.HttpLoggingKey;
import org.naho.shared.exception.*;
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
    private final MessageService messageService;
    private final ProblemDetailHelper problemDetailHelper;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleApplicationException(
            ApplicationException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCodeHttpMapper.toStatus(errorCode);

        // create problem detail
        ProblemDetail problemDetail = problemDetailHelper.buildProblemDetail(errorCode, e, request);

        // logging
        // set http fields
        ThreadContext.put(HttpLoggingKey.HTTP_STATUS_CODE, String.valueOf(httpStatus));

        // set error fields
        ThreadContext.put(ErrorLoggingKey.ERROR_CODE, errorCode.getCode());
        ThreadContext.put(ErrorLoggingKey.ERROR_MESSAGE, messageService.getMessage(e.getMessage()));
        ThreadContext.put(ErrorLoggingKey.ERROR_TYPE, e.getClass().getSimpleName());

        log.warn(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonApplicationErrorCode.COMMON_INTERNAL_SERVER_ERROR;

        // create problem detail
        ProblemDetail problemDetail = problemDetailHelper.buildProblemDetail(errorCode, e, request);

        // logging
        // set http fields
        ThreadContext.put(HttpLoggingKey.HTTP_STATUS_CODE, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));

        // set error fields
        ThreadContext.put(ErrorLoggingKey.ERROR_CODE, errorCode.getCode());
        ThreadContext.put(ErrorLoggingKey.ERROR_MESSAGE, e.getMessage());
        ThreadContext.put(ErrorLoggingKey.ERROR_TYPE, e.getClass().getSimpleName());

        log.error(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}

