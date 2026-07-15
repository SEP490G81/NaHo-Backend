package org.naho.shared.response;

public record FieldErrorResponse(
        String field,
        String message
) {
}
