package org.naho.user.constant;

public enum UserSortColumn {
    ID("id"),
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
