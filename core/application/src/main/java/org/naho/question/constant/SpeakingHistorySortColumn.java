package org.naho.question.constant;

public enum SpeakingHistorySortColumn {
    CREATED_TIME("createdTime");

    private final String columnName;

    SpeakingHistorySortColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
