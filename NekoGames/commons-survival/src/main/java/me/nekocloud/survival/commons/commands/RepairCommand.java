package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand extends CommonsCommand {

    public RepairCommand(ConfigData configData) {
        super(configData, true, "repair", "fix");
        setMinimalGroup(configData.getInt("repairCommand"));
        spigotCommand.setCooldown(configData.getInt("repairCooldown"), getCooldownType());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR){
            gamerEntity.sendMessageLocale("REPAIR_ERROR");
            return;
        }

        if (item.getType().isBlock() || item.getType().getMaxDurability() < 1){
            gamerEntity.sendMessageLocale("REPAIR_ERROR");
            return;
        }

        if (item.getDurability() == 0){
            sendMessageLocale(gamerEntity, "REPAIR");
            return;
        }

        item.setDurability((short)0);
        sendMessageLocale(gamerEntity, "REPAIR");
    }
}
