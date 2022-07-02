package me.nekocloud.skyblock.command;

import com.google.common.collect.ImmutableList;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.api.manager.SkyGamerManager;
import me.nekocloud.skyblock.craftisland.CraftSkyGamer;
import me.nekocloud.skyblock.gui.guis.RequestIslandGui;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.SkyBlockGui;
import me.nekocloud.skyblock.api.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class RequestCommand implements CommandInterface, CommandTabComplete {

    private final GuiManager<SkyBlockGui> guiManager = SkyBlockAPI.getSkyGuiManager();
    final static IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();
    final static SkyGamerManager MANAGER = SkyBlockAPI.getSkyGamerManager();
    final static GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    RequestCommand(String commandName, String... aliases) {
        SpigotCommand command = COMMANDS_API.register(commandName, this, aliases);
        command.setOnlyPlayers(true);
        command.setCooldown(25, "request_cooldown");
        command.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Island island = ISLAND_MANAGER.getIsland(gamer);

        if (island != null) {
            gamer.sendMessageLocale("ISLAND_REQUESTS_ERROR");
            return;
        }

        Player sender = gamer.getPlayer();
        CraftSkyGamer skyGamer = (CraftSkyGamer) MANAGER.getSkyGamer(sender);
        if (skyGamer == null)
            return;

        Map<String, Long> playerRequest = skyGamer.getRequests();
        Player player = null;
        for (String name : playerRequest.keySet()) {
            player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                playerRequest.remove(name);
            }
        }

        if (playerRequest.size() == 1) {
            if (player != null) {
                accept(sender, player);
            }
            return;
        } else if (playerRequest.size() == 0) {
            sendMessage(gamerEntity, "TPACCEPT_ERROR");
            return;
        } else if (args.length == 0){
            RequestIslandGui gui = guiManager.getGui(RequestIslandGui.class, sender);
            if (gui != null) {
                gui.open();
            }
            return;
        }

        String name = args[0];
        player = Bukkit.getPlayer(name);
        if (player == null) {
            COMMANDS_API.playerOffline(gamerEntity, name);
            return;
        }

        if (!playerRequest.containsKey(name)) {
            sendMessage(gamerEntity, "TPACCEPT_ERROR_PLAYER", name);
            return;
        }
        accept(sender, player);
    }

    static void sendMessage(GamerEntity gamerEntity, String key, Object... replaced) {
        Language language = gamerEntity.getLanguage();
        gamerEntity.sendMessage(SkyBlockAPI.getPrefix() + language.getMessage(key, replaced));
    }

    @Override
    public final List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        CraftSkyGamer skyGamer = (CraftSkyGamer) MANAGER.getSkyGamer(gamerEntity.getName());
        if (skyGamer == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(skyGamer.getRequests().keySet(), strings);
    }

    abstract void accept(Player sender, Player who);
}
