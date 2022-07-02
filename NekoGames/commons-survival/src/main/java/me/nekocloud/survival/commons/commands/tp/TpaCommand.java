package me.nekocloud.survival.commons.commands.tp;

import com.google.common.collect.ImmutableList;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.TpacceptGui;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class TpaCommand extends CommonsCommand implements CommandTabComplete {

    TpaCommand(ConfigData configData, String command, String... aliases) {
        super(configData, true, command, aliases);
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        CraftUser user = (CraftUser) USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        Map<String, Long> playerRequest = user.getCallReguests();
        Player player = null;
        for (String name : playerRequest.keySet()) {
            player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                playerRequest.remove(name);
            }
        }

        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();
        if (playerRequest.size() == 1) {
            if (player != null) {
                accept(sender, player);
            }
            return;
        } else if (playerRequest.size() == 0) {
            sendMessageLocale(gamerEntity, "TPACCEPT_ERROR");
            return;
        } else if (strings.length == 0) {
            TpacceptGui tpacceptGui = GUI_MANAGER.getGui(TpacceptGui.class, sender);
            if (tpacceptGui != null) {
                tpacceptGui.open();
            }
            return;
        }

        String name = strings[0];
        player = Bukkit.getPlayer(name);
        if (player == null) {
            COMMANDS_API.playerOffline(gamerEntity, name);
            return;
        }

        if (!playerRequest.containsKey(name)) {
            sendMessageLocale(gamerEntity, "TPACCEPT_ERROR_PLAYER", name);
            return;
        }
        accept(sender, player);
    }

    @Override
    public final List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        CraftUser user = (CraftUser) USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(user.getCallReguests().keySet(), strings);
    }

    protected abstract void accept(Player sender, Player who);
}
