package org.naho.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.naho.i18n.MessageService;
import org.naho.shared.constant.ProblemDetailProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProblemDetailHelper {
    private final ErrorCodeHttpMapper errorCodeHttpMapper;
    private final MessageService messageService;

    public ProblemDetail buildProblemDetail(ErrorCode errorCode, Exception e, HttpServletRequest request) {
        HttpStatus status = errorCodeHttpMapper.toStatus(errorCode);
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);

        // set default fields
        problemDetail.setType(errorCodeHttpMapper.toType(errorCode));
        problemDetail.setTitle(messageService.getMessage(errorCode.getTitleKey()));
        problemDetail.setDetail(messageService.getMessage(e.getMessage()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        // set custom fields
        problemDetail.setProperty(ProblemDetailProperty.ERROR_CODE, errorCode.getCode());
        problemDetail.setProperty(ProblemDetailProperty.TRACE_ID, ThreadContext.get(ProblemDetailProperty.TRACE_ID));
        problemDetail.setProperty(ProblemDetailProperty.TIMESTAMP, Instant.now().toString());
        return problemDetail;
    }
}
