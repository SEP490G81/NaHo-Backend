package org.naho.shared.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ProblemDetailResponse extends ProblemDetail {

    private String errorCode;

    private String traceId;

    private String timestamp;

    private List<FieldErrorResponse> fieldErrors;

    protected ProblemDetailResponse() {
        super();
    }

    protected ProblemDetailResponse(ProblemDetail other) {
        super(other);
    }

    public static Builder builder(HttpStatus status) {
        return new Builder(status);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public static class Builder {

        private final ProblemDetailResponse response;

        private Builder(HttpStatus status) {
            this.response = new ProblemDetailResponse(
                    ProblemDetail.forStatus(status)
            );
        }

        public Builder type(URI type) {
            response.setType(type);
            return this;
        }

        public Builder title(String title) {
            response.setTitle(title);
            return this;
        }

        public Builder detail(String detail) {
            response.setDetail(detail);
            return this;
        }

        public Builder instance(URI instance) {
            response.setInstance(instance);
            return this;
        }

        public Builder errorCode(String errorCode) {
            response.errorCode = errorCode;
            return this;
        }

        public Builder traceId(String traceId) {
            response.traceId = traceId;
            return this;
        }

        public Builder timestamp(String timestamp) {
            response.timestamp = timestamp;
            return this;
        }

        public Builder fieldErrors(List<FieldErrorResponse> fieldErrors) {
            response.fieldErrors = fieldErrors;
            return this;
        }

        public Builder addFieldError(FieldErrorResponse fieldError) {
            if (response.fieldErrors == null) {
                response.fieldErrors = new ArrayList<>();
            }
            response.fieldErrors.add(fieldError);
            return this;
        }

        public ProblemDetailResponse build() {
            return response;
        }
    }
}
