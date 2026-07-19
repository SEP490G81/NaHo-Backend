package org.naho.chest.exception;

import org.naho.i18n.message.chest.ChestTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ChestDomainErrorCode implements ErrorCode {
    CHEST_TITLE_EMPTY(
            "CHEST_001",
            ChestTitleMessageKey.CHEST_CREATION_FAILED_TITLE,
            400
    ),
    CHEST_POINT_INVALID(
            "CHEST_002",
            ChestTitleMessageKey.CHEST_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ChestDomainErrorCode(String code, String titleKey, int statusCode) {
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
