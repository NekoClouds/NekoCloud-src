package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearCommand extends CommonsCommand {

    public ClearCommand(ConfigData configData) {
        super(configData,true, "clear", "ci", "clearinventory", "clean");
        setMinimalGroup(configData.getInt("clearCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            clear(player);
            send("CLEAR_INV", gamer, player.getDisplayName());
        } else {
            clear(sender);
        }
    }

    private void clear(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        send("CLEAR_INV", gamer, null);

    }
}
