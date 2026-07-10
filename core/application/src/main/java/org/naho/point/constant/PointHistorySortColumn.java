package org.naho.point.constant;

public enum PointHistorySortColumn {
    POINT("point"),
    TRANSACTION_TIME("transactionTime");

    PointHistorySortColumn(String columnName) {
        this.columnName = columnName;
    }

    private final String columnName;

    public String getColumnName() {
        return columnName;
    }
}
