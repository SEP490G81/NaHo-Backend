package org.naho.logging;

public final class ContextLoggingKey {
    private ContextLoggingKey() {
    }

    public static final String TRACE_ID = "traceId";
    public static final String USER_ID = "userId";
    public static final String USER_ROLE = "userRole";
}
