package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.CommonWords;

public final class ListCommand implements CommandInterface {

    public ListCommand() {
        COMMANDS_API.register("list", this,
                "список", "игроки", "players");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        val list = new StringBuilder();

        int size = 0;
        for (val all : GAMER_MANAGER.getGamers().values()) {
            list.append(all.getDisplayName()).append("§f, ");
            size++;
        }

        val lang = gamerEntity.getLanguage();
        val prefix = "§d" + GAMER_MANAGER.getSpigot().getName().toUpperCase();

        gamerEntity.sendMessageLocale("LIST_CMD",
                prefix,
                String.valueOf(size),
                CommonWords.PLAYERS_1.convert(size, lang),
                list.substring(0, list.length() - 4));
    }
}
