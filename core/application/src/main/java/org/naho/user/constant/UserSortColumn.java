package org.naho.user.constant;

public enum UserSortColumn {
    EMAIL("email"),
    USERNAME("username"),
    FULL_NAME("fullName"),
    DOB("dob");

    private final String columnName;

    UserSortColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
