package org.naho.logging;

public class HttpLoggingKey {
    public static final String HTTP_ROUTE = "httpRoute";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_CLIENT_IP = "httpClientIp";
    public static final String HTTP_DURATION_MS = "httpDurationMs";
    public static final String HTTP_STATUS_CODE = "httpStatusCode";

    private HttpLoggingKey() {
    }
}
