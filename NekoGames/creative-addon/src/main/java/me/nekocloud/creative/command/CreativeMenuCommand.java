package me.nekocloud.creative.command;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.creative.gui.CreativeMenuGui;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreativeMenuCommand implements CommandInterface {

    private final Map<Integer, CreativeMenuGui> menus;

    public CreativeMenuCommand(Map<Integer, CreativeMenuGui> menus) {
        this.menus = menus;
        SpigotCommand spigotCommand = COMMANDS_API.register("cm", this, "creativemenu",
                "креатив", "creative");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Language lang = gamerEntity.getLanguage();

        menus.getOrDefault(lang.getId(), menus.get(Language.DEFAULT.getId()))
                .open(player);
    }
}
