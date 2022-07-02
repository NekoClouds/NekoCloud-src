package me.nekocloud.anarchy.command;

import gnu.trove.map.TIntObjectMap;
import me.nekocloud.anarchy.gui.AnarchyMenuGui;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;

public class AnarchystartCommand implements CommandInterface {

    private static final int LANGUAGE_DAFAULT_ID = Language.DEFAULT.getId();

    private final TIntObjectMap<AnarchyMenuGui> menus;

    public AnarchystartCommand(TIntObjectMap<AnarchyMenuGui> menus) {
        this.menus = menus;

        SpigotCommand command = COMMANDS_API.register("anarchystart", this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        AnarchyMenuGui anarchyMenuGui = menus.get(gamer.getLanguage().getId());
        if (anarchyMenuGui != null) {
            anarchyMenuGui.open(player);
            return;
        }

        menus.get(LANGUAGE_DAFAULT_ID).open(player);
    }
}
