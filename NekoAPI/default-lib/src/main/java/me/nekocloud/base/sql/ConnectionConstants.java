package me.nekocloud.base.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConnectionConstants {

    DOMAIN("127.0.0.1"),
    PASSWORD("P39JqphCQOjWF0dei3CQfn5r");

    private final String value;
}
