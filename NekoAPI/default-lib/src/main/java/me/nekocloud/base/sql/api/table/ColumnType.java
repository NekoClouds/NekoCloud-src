package me.nekocloud.base.sql.api.table;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ColumnType {
    INT("INT"),
    INT_11("INT(11)"),
    INT_2("INT(2)"),
    INT_5("INT(5)"),
    TINY_INT("TINYINT"),
    TINY_INT_2("TINYINT(2)"),
    VARCHAR_16("VARCHAR(16)"),
    VARCHAR_32("VARCHAR(32)"),
    VARCHAR_48("VARCHAR(48)"),
    VARCHAR_64("VARCHAR(64)"),
    VARCHAR_128("VARCHAR(128)"),
    TEXT("TEXT(0)"),
    BIG_INT("BIGINT(30)"),
    BOOLEAN("BOOLEAN"),
    DOUBLE("DOUBLE"),
    TIMESTAMP("TIMESTAMP"),
    DATETIME("DATETIME"),
    ;

    private final String sql;
}
