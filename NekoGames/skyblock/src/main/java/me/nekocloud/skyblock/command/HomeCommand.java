package me.nekocloud.skyblock.command;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.gui.guis.ChoisedGui;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.SkyBlockGui;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.module.HomeModule;
import org.bukkit.entity.Player;

public final class HomeCommand implements CommandInterface {
    private final IslandManager manager = SkyBlockAPI.getIslandManager();
    private static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();

    public HomeCommand() {
        SpigotCommand command = NekoCloud.getCommandsAPI().register("home", this,
                "go", "домой", "start");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Island island = manager.getIsland(player);

        toHome(player, gamer, island);
    }

    static void toHome(Player player, BukkitGamer gamer, Island island) {
        if (island == null) {
            ChoisedGui choisedGui = SKY_GUI_MANAGER.getGui(ChoisedGui.class, player);
            if (choisedGui != null)
                choisedGui.open();
            return;
        }

        HomeModule homeModule = island.getModule(HomeModule.class);
        if (homeModule == null) {
            return;
        }

        homeModule.teleport(player);
    }
}
