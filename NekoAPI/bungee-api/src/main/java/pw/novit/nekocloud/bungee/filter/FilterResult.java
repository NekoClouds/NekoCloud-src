package pw.novit.nekocloud.bungee.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Written by _Novit_ for ChatFilter in 03 2022
 */
@Getter
@Setter
@AllArgsConstructor
public class FilterResult {

    private String message;
    private boolean cancelled;

    public FilterResult(String def) {
        message = def;
        cancelled = false;
    }
}
