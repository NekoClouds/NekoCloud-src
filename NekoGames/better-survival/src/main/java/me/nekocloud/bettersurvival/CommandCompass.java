package me.nekocloud.bettersurvival;

import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandCompass implements CommandInterface {

    public CommandCompass() {
        SpigotCommand spigotCommand = COMMANDS_API.register("compass", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        Player player = ((BukkitGamer) gamerEntity).getPlayer();
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Language lang = gamer.getLanguage();
        switch (args.length) {
            case 0: {
                Location location = BetterSurvival.getBedLocation(player);

                if (location == null || location.getWorld().getName().equalsIgnoreCase("lobby")) {
                    player.setCompassTarget(player.getWorld().getSpawnLocation());

                    player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS_WITHOUT_BED"));
                    break;
                }
                player.setCompassTarget(location);
                player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS_ERROR_FORMAT"));
                break;
            }
            case 2: {
                try {
                    int x = Integer.parseInt(args[0]);
                    int z = Integer.parseInt(args[1]);
                    player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS", x, z));
                    player.setCompassTarget(new Location(player.getWorld(), x, 64.0, z));
                } catch (NumberFormatException ex) {
                    player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS_ERROR_FORMAT"));
                }
                break;
            }
            case 3: {
                try {
                    int x = Integer.parseInt(args[0]);
                    int z = Integer.parseInt(args[2]);
                    player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS", x, z));
                    player.setCompassTarget(new Location(player.getWorld(),  x, 64.0,  z));
                } catch (NumberFormatException ex) {
                    player.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_COMPASS_ERROR_FORMAT"));
                }
                break;
            }
        }
    }
}
