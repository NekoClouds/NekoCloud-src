package me.nekocloud.lobby.game.top;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.base.locale.CommonWords;

@Getter
@AllArgsConstructor
public class TopTable {

    public static final String DEFAULT_COLUMN = "Wins";

    public static final String DEFAULT_LOCALE_TOP = "HOLO_TOP";

    private final String table;
    private final String holoName;

    private final CommonWords commonWords;

    private final StatsType type;

    private final String column;
    private final String localeKey;

}
