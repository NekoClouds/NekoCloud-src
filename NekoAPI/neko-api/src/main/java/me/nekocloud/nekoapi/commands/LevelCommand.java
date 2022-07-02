package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import me.nekocloud.base.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public final class LevelCommand implements CommandInterface {

    public LevelCommand() {
        val spigotCommand = COMMANDS_API.register("level", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(
            final @NotNull GamerEntity gamerEntity,
            final String command,
            final String[] args) {
        val gamer = (BukkitGamer) gamerEntity;

        gamerEntity.sendMessagesLocale("LEVEL_COMMAND",
                StringUtil.getNumberFormat(gamer.getLevelNetwork()),
                StringUtil.onPercentBar(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                        NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)),
                StringUtil.onPercent(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                        NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)) + "%",
                StringUtil.getNumberFormat(gamer.getExpNextLevel()));
    }
}
