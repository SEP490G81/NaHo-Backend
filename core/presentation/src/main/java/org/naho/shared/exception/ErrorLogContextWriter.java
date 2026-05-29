package org.naho.shared.exception;

import org.apache.logging.log4j.ThreadContext;
import org.naho.shared.constant.ErrorLoggingKey;
import org.naho.shared.constant.HttpLoggingKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogContextWriter {
    public void write(
            HttpStatus status,
            ErrorCode errorCode,
            Throwable exception,
            String errorMessage
    ) {
        ThreadContext.put(
                HttpLoggingKey.HTTP_STATUS_CODE,
                String.valueOf(status.value())
        );

        ThreadContext.put(ErrorLoggingKey.ERROR_CODE, errorCode.getCode());
        ThreadContext.put(ErrorLoggingKey.ERROR_MESSAGE, errorMessage);
        ThreadContext.put(ErrorLoggingKey.ERROR_TYPE, exception.getClass().getSimpleName());
    }
}
