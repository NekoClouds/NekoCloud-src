package me.nekocloud.api.command;

import me.nekocloud.api.player.GamerEntity;

import java.util.List;

public interface CommandTabComplete {

    /**
     * Выполнить табкомплит
     */
    List<String> getComplete(GamerEntity gamerEntity, String alias, String[] args);
}
