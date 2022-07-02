package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.JSONMessageAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.util.JsonBuilder;
import org.jetbrains.annotations.NotNull;

public final class RulesCommand implements CommandInterface {

    private final String url = "https://forum.nekocloud.me/";
    private final JSONMessageAPI jsonMessageAPI = NekoCloud.getJsonMessageAPI();

    public RulesCommand() {
        COMMANDS_API.register("rules", this);
    }

    @Override
    public void execute(
            final @NotNull GamerEntity gamerEntity,
            final String command,
            final String[] args
    ) {
        val lang = gamerEntity.getLanguage();
        val player = ((BukkitGamer) gamerEntity).getPlayer();

        jsonMessageAPI.send(player, new JsonBuilder()
                .addText(lang.getMessage( "RULES_COMMAND"))
                .addOpenUrl("§d" + url, url, lang.getMessage("RULES_COMMAND_HOVER"))
                .toString());
    }
}
