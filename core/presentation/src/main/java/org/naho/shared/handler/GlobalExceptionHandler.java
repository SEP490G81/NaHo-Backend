package org.naho.shared.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.MessageService;
import org.naho.shared.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorCodeHttpMapper errorCodeHttpMapper;
    private final MessageService messageService;
    private final ProblemDetailFactory problemDetailFactory;
    private final ErrorLogContextWriter errorLogContextWriter;

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ProblemDetail> handleApplicationException(
            BaseException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCodeHttpMapper.toStatus(errorCode);
        String errorMessage = messageService.getMessage(e.getMessage(), e.getArgs());

        ProblemDetail problemDetail = problemDetailFactory.create(
                errorCode.getCode(),
                messageService.getMessage(errorCode.getTitleKey()),
                status,
                errorCodeHttpMapper.toType(errorCode),
                errorMessage,
                request
        );

        errorLogContextWriter.write(
                status,
                errorCode,
                e,
                errorMessage
        );

        log.warn(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonApplicationErrorCode.COMMON_INTERNAL_SERVER_ERROR;

        HttpStatus status = errorCodeHttpMapper.toStatus(errorCode);
        String errorMessage = messageService.getMessage(e.getMessage());

        ProblemDetail problemDetail = problemDetailFactory.create(
                errorCode.getCode(),
                messageService.getMessage(errorCode.getTitleKey()),
                status,
                errorCodeHttpMapper.toType(errorCode),
                errorMessage,
                request
        );

        errorLogContextWriter.write(
                status,
                errorCode,
                e,
                errorMessage
        );

        log.error(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(status).body(problemDetail);
    }
}

