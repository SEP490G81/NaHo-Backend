package org.naho.payment.constant;

public enum PaymentOrderSortColumn {
    ID("id"),
    ORDER_CODE("orderCode"),
    USER_ID("userId"),
    AMOUNT("amountAmount"),
    PROVIDER("provider"),
    STATUS("status"),
    CREATED_TIME("createdTime"),
    EXPIRES_TIME("expiresTime"),
    PAID_TIME("paidTime");

    private final String columnName;

    PaymentOrderSortColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
