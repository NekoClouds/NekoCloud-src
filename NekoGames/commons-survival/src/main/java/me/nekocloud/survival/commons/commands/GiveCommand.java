package me.nekocloud.survival.commons.commands;

import lombok.val;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.survival.commons.config.ConfigData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class GiveCommand extends CommonsCommand{

	public GiveCommand(ConfigData configData) {
		super(configData, false, "give", "выдать");
		setMinimalGroup(Group.OWNER); // так надо сука блядь
	}

	@Override
	public void execute(GamerEntity gamerEntity, String command, String[] args) {
		if (args.length < 3) {
			gamerEntity.sendMessage("use: /give player id amount");
		} else {
			val target = Bukkit.getPlayerExact(args[0]);
			if (target == null) {
				gamerEntity.sendMessage("player not online");
			} else if (target.getInventory().firstEmpty() == -1) {
				gamerEntity.sendMessage("player inventory is full");
			} else {
				ItemStack item = ItemUtil.getBuilder(Material.getMaterial(args[1])).setAmount(Integer.parseInt(args[2])).build();
				if (item.getType() == Material.ENCHANTED_BOOK) {
					val meta = (EnchantmentStorageMeta)item.getItemMeta();
					meta.addStoredEnchant(Enchantment.getByName(args[3].split(":")[0].toUpperCase()), Integer.parseInt(args[3].split(":")[1]), false);
					item.setItemMeta(meta);
				}

				if (args.length > 3) {
					val builder = ItemUtil.getBuilder(item);
					for(int i = 3; i < args.length; ++i) {
						builder.addEnchantment(Enchantment.getByName(args[i].split(";")[0]),
								Integer.parseInt(args[i].split(";")[1]));
					}
					item = builder.build();
				}

				target.getInventory().addItem(item);
			}
		}
	}
}
