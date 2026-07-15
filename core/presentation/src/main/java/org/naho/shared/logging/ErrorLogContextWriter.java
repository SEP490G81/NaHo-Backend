package org.naho.shared.logging;

import org.apache.logging.log4j.ThreadContext;
import org.naho.logging.ErrorLoggingKey;
import org.naho.logging.HttpLoggingKey;
import org.naho.shared.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public final class ErrorLogContextWriter {
    private ErrorLogContextWriter() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpStatus status;
        private ErrorCode errorCode;
        private Throwable exception;
        private String errorMessage;

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public void write() {

            if (status != null) {
                ThreadContext.put(
                        HttpLoggingKey.HTTP_STATUS_CODE,
                        String.valueOf(status.value())
                );
            }

            if (errorCode != null) {
                ThreadContext.put(
                        ErrorLoggingKey.ERROR_CODE,
                        errorCode.getCode()
                );
            }

            if (errorMessage != null) {
                ThreadContext.put(
                        ErrorLoggingKey.ERROR_MESSAGE,
                        errorMessage
                );
            }

            if (exception != null) {
                ThreadContext.put(
                        ErrorLoggingKey.ERROR_TYPE,
                        exception.getClass().getSimpleName()
                );

                ThreadContext.put(
                        ErrorLoggingKey.ERROR_STACKTRACE,
                        Arrays.toString(exception.getStackTrace())
                );
            }
        }
    }
}