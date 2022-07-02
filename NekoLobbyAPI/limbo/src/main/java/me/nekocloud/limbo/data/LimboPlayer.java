package me.nekocloud.limbo.data;

import lombok.Getter;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;

@Getter
public final class LimboPlayer {

    private final String name;

    private final int playerID;
    private final Group group;
    private final String prefix;
    private final Language language;

    public LimboPlayer(String name) {
        this.name = name;

        playerID = GlobalLoader.containsPlayerID(name);
        if (playerID == -1) {
            group = Group.DEFAULT;
            prefix = group.getPrefix();
            language = Language.DEFAULT;

            return;
        }

        group = GlobalLoader.getGroup(playerID);
        prefix = GlobalLoader.getPrefix(playerID, group);
        language = GlobalLoader.getLanguage(playerID);
    }

    public String getDisplayName() {
        return "§r" + getPrefix() + name;
    }
}
