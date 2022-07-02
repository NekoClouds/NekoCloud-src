package me.nekocloud.survival.commons.commands.get;

import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.util.Head;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadCommand extends CommonsCommand {

    public HeadCommand(ConfigData configData) {
        super(configData, true, "head");
        setMinimalGroup(configData.getInt("headCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer  = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (strings.length == 0) {
            ItemStack head = gamer.getHead();
            player.getInventory().addItem(head);
            return;
        }

        String name = strings[0];
        if (!name.matches("[a-zA-Z0-9_]+")) {
            gamer.sendMessageLocale("NAME_IS_INVALID");
            return;
        }

        BukkitGamer headGamer = GAMER_MANAGER.getGamer(name);
        if (headGamer != null) {
            ItemStack head = headGamer.getHead();
            player.getInventory().addItem(head);
            return;
        }

        BukkitUtil.runTaskAsync(() -> {
            int playerID = GlobalLoader.containsPlayerID(name);
            ItemStack head;
            if (playerID == -1) {
                head = new ItemStack(Material.SKULL_ITEM);
                head.setDurability((short) 3);
            } else {
                String value = GlobalLoader.getSelectedSkin(name, playerID);
                head = Head.getHeadByValue(value);
            }

            BukkitUtil.runTask(() -> player.getInventory().addItem(head));
        });
    }
}
