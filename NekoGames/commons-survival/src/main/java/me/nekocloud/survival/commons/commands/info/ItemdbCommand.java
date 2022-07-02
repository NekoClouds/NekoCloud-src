package me.nekocloud.survival.commons.commands.info;

import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemdbCommand extends CommonsCommand {

    public ItemdbCommand(ConfigData configData) {
        super(configData, true, "itemdb", "dura", "durability", "itemno");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Language lang = gamer.getLanguage();
        ItemStack itemStack = gamer.getPlayer().getInventory().getItemInMainHand();

        if (itemStack == null){
            gamerEntity.sendMessageLocale("ITEMDB_ERROR");
            return;
        }

        String message = lang.getMessage("ITEMDB",
                itemStack.getType().toString(),
                String.valueOf(itemStack.getTypeId()));

        if (itemStack.getType() != Material.AIR && itemStack.getDurability() != 0)
            message += ":" + itemStack.getDurability();

        sendMessage(gamerEntity, message);
    }
}
