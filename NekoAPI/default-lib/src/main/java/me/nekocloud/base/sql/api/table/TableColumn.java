package me.nekocloud.base.sql.api.table;

import lombok.Getter;

@Getter
public final class TableColumn {
    private final String name;

    private final ColumnType columnType;

    private boolean nullValue;
    private boolean primaryKey;
    private boolean unigue;
    private boolean autoIncrement;

    private Object defaultValue;

    public TableColumn(String name, ColumnType columnType) {
        this.name = name;
        this.columnType = columnType;
    }

    public TableColumn setNull(boolean nullValue) {
        this.nullValue = nullValue;
        return this;
    }

    public TableColumn setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public TableColumn primaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public TableColumn unigue(boolean unigue) {
        this.unigue = unigue;
        return this;
    }

    public TableColumn autoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    private String getDefaultValueString() {
        return defaultValue == null ? "" : "'" + this.defaultValue.toString() + "'";
    }

    @Override
    public String toString() {
        return "`" + this.name + "` " + this.columnType.getSql() + (this.nullValue ? "" : " NOT NULL")
                + (!this.unigue ? "" : " UNIQUE") + (this.defaultValue == null ? "" : " DEFAULT "
                + this.getDefaultValueString()) + (!this.autoIncrement ? "" : " AUTO_INCREMENT");
    }
}
