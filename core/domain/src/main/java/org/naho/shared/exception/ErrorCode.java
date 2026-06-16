package org.naho.shared.exception;

import java.net.URI;

public interface ErrorCode {
    String getCode();

    String getTitleKey();

    int getStatusCode();

    default URI getTypeUri() {
        return URI.create("https://api.v1.naho/errors/" + getCode().toLowerCase());
    }
}
