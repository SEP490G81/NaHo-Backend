package org.naho.shared.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.naho.i18n.MessageService;
import org.naho.logging.ContextLoggingKey;
import org.naho.shared.exception.BaseException;
import org.naho.shared.exception.CommonErrorCode;
import org.naho.shared.exception.ErrorCode;
import org.naho.shared.logging.ErrorLogContextWriter;
import org.naho.shared.response.FieldErrorResponse;
import org.naho.shared.response.ProblemDetailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ProblemDetailResponse> handleApplicationException(
            BaseException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());
        String errorMessage = messageService.getMessage(e.getMessage(), e.getArgs());

        ProblemDetailResponse response = ProblemDetailResponse
                .builder(status)
                .type(errorCode.getTypeUri())
                .title(messageService.getMessage(errorCode.getTitleKey()))
                .detail(errorMessage)
                .instance(URI.create(request.getRequestURI()))
                .errorCode(errorCode.getCode())
                .traceId(ThreadContext.get(ContextLoggingKey.TRACE_ID))
                .timestamp(Instant.now().toString())
                .build();

        ErrorLogContextWriter.builder()
                .status(status)
                .errorCode(errorCode)
                .exception(e)
                .errorMessage(errorMessage)
                .write();

        log.warn(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonErrorCode.COMMON_INVALID_REQUEST;
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());

        List<FieldErrorResponse> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(
                        fieldError.getField(),
                        messageService.getMessage(fieldError.getDefaultMessage())
                ))
                .toList();

        ProblemDetailResponse response = ProblemDetailResponse
                .builder(status)
                .type(errorCode.getTypeUri())
                .title(messageService.getMessage(errorCode.getTitleKey()))
                .instance(URI.create(request.getRequestURI()))
                .errorCode(errorCode.getCode())
                .traceId(ThreadContext.get(ContextLoggingKey.TRACE_ID))
                .timestamp(Instant.now().toString())
                .fieldErrors(fieldErrors)
                .build();

        ErrorLogContextWriter.builder()
                .status(status)
                .errorCode(errorCode)
                .exception(e)
                .write();

        log.warn(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetailResponse> handleNoResourceFoundException(
            NoResourceFoundException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonErrorCode.COMMON_RESOURCE_NOT_FOUND;
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());
        String errorMessage = e.getMessage();

        ProblemDetailResponse response = ProblemDetailResponse
                .builder(status)
                .type(errorCode.getTypeUri())
                .title(messageService.getMessage(errorCode.getTitleKey()))
                .detail(errorMessage)
                .instance(URI.create(request.getRequestURI()))
                .errorCode(errorCode.getCode())
                .traceId(ThreadContext.get(ContextLoggingKey.TRACE_ID))
                .timestamp(Instant.now().toString())
                .build();

        ErrorLogContextWriter.builder()
                .status(status)
                .errorCode(errorCode)
                .exception(e)
                .errorMessage(errorMessage)
                .write();

        log.warn("{}: {}", messageService.getMessage(errorCode.getTitleKey()), e.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR;

        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());
        String errorMessage = messageService.getMessage(e.getMessage());

        ProblemDetailResponse response = ProblemDetailResponse
                .builder(status)
                .type(errorCode.getTypeUri())
                .title(messageService.getMessage(errorCode.getTitleKey()))
                .detail(errorMessage)
                .instance(URI.create(request.getRequestURI()))
                .errorCode(errorCode.getCode())
                .traceId(ThreadContext.get(ContextLoggingKey.TRACE_ID))
                .timestamp(Instant.now().toString())
                .build();

        ErrorLogContextWriter.builder()
                .status(status)
                .errorCode(errorCode)
                .exception(e)
                .errorMessage(errorMessage)
                .write();

        log.error(messageService.getMessage(errorCode.getTitleKey()), e);
        return ResponseEntity.status(status).body(response);
    }
}

