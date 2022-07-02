package me.nekocloud.skyblock.command;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.achievements.gui.AchievementGui;
import me.nekocloud.nekoapi.achievements.manager.AchievementManager;
import org.bukkit.entity.Player;

public final class AchievementCommand implements CommandInterface {

    private final AchievementManager achievementManager;
    private final SpigotCommand command;

    @Deprecated //todo удалить
    public AchievementCommand(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;

        this.command = COMMANDS_API.register("achievement", this, "ach");
        this.command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        AchievementGui achievementGui = new AchievementGui(player, "ISLAND_ACHIEVEMENT_GUI_NAME");
        achievementGui.setItems(achievementManager);
        achievementGui.addBackItem((clicker, clickType, slot) -> clicker.chat("/is"));
        achievementGui.open();
    }
}
