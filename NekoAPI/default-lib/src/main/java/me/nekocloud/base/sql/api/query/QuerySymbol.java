package me.nekocloud.base.sql.api.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuerySymbol {
    EQUALLY("="),
    MORE_OR_EQUAL(">="),
    MORE(">"),
    LESS("<"),
    LESS_OR_EQUAL("<="),
    NOT_EQUAL("!=");

    private final String symbol;
}
