package org.naho.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.naho.shared.constant.ProblemDetailProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProblemDetailFactory {
    public ProblemDetail create(
            String errorCode,
            String title,
            HttpStatus status,
            URI type,
            String detailMessage,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);

        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detailMessage);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        problemDetail.setProperty(ProblemDetailProperty.ERROR_CODE, errorCode);
        problemDetail.setProperty(
                ProblemDetailProperty.TRACE_ID,
                ThreadContext.get(ProblemDetailProperty.TRACE_ID)
        );
        problemDetail.setProperty(
                ProblemDetailProperty.TIMESTAMP,
                Instant.now().toString()
        );

        return problemDetail;
    }

}
