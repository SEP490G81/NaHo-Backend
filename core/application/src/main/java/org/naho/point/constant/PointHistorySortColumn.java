package org.naho.point.constant;

public enum PointHistorySortColumn {
    POINT("point"),
    TRANSACTION_TIME("transactionTime");

    private final String columnName;

    PointHistorySortColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
