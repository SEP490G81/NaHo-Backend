package org.naho.notification.exception;

import org.naho.i18n.message.notification.NotificationTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum NotificationDomainErrorCode implements ErrorCode {
    NOTIFICATION_USER_ID_NOT_VALID(
            "NOTIFICATION_001",
            NotificationTitleMessageKey.NOTIFICATION_USER_ID_NOT_VALID_TITLE,
            400
    ),
    NOTIFICATION_TYPE_NOT_VALID(
            "NOTIFICATION_002",
            NotificationTitleMessageKey.NOTIFICATION_TYPE_NOT_VALID_TITLE,
            400
    ),
    NOTIFICATION_TITLE_NOT_VALID(
            "NOTIFICATION_003",
            NotificationTitleMessageKey.NOTIFICATION_TITLE_NOT_VALID_TITLE,
            400
    ),
    NOTIFICATION_CONTENT_NOT_VALID(
            "NOTIFICATION_004",
            NotificationTitleMessageKey.NOTIFICATION_CONTENT_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    NotificationDomainErrorCode(String code, String titleKey, int statusCode) {
        this.code = code;
        this.titleKey = titleKey;
        this.statusCode = statusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
