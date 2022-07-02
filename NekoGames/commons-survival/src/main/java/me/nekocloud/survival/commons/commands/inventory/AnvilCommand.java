package me.nekocloud.survival.commons.commands.inventory;

import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.entity.Player;

public class AnvilCommand extends CommonsCommand {

    private final NmsManager nmsManager = NmsAPI.getManager();

    public AnvilCommand(ConfigData configData) {
        super(configData, true, "anvil");
        setMinimalGroup(configData.getInt("anvilCommand"));
        spigotCommand.setCooldown(60, getCooldownType());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();

        nmsManager.getAnvil(player).openGui();
    }
}
