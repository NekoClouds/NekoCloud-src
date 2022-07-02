package me.nekocloud.games.customitems.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.games.customitems.api.CustomItem;
import me.nekocloud.games.customitems.api.CustomItemsAPI;
import me.nekocloud.games.customitems.manager.CustomItemsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemsCommand implements CommandInterface {

    private final CustomItemsManager manager = CustomItemsAPI.getItemsManager();

    public CustomItemsCommand() {
        val spigotCommand = COMMANDS_API.register("moreitems", this);
        spigotCommand.setGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        if (args.length < 4 || !args[0].equalsIgnoreCase("give")) {
            gamerEntity.sendMessageLocale("MORE_ITEMS_COMMAND_USE");
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            gamerEntity.sendMessageLocale("PLAYER_NOT_ONLINE", args[1]);
            return;
        }
        CustomItem customItem = this.manager.getItem(args[2]);
        if (customItem == null) {
            gamerEntity.sendMessageLocale("MORE_ITEMS_ITEM_NOT_FOUND", args[2]);
            return;
        }
//        if (!NumberUtils.isInt(args[3])) {
//            gamerEntity.sendMessageLocale("MORE_ITEMS_AMOUNT_NOT_CORRECT"));
//            return;
//        }

        ItemStack item = customItem.getItem().clone();
        item.setAmount(Integer.parseInt(args[3]));
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }
}
