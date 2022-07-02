package me.nekocloud.siteshop.item;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandsAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.siteshop.ItemsLoader;
import me.nekocloud.siteshop.SiteShop;
import me.nekocloud.siteshop.commands.SiteShopCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class SSItemManager {

    private final CommandsAPI commandsAPI = NekoCloud.getCommandsAPI();

    private final TIntObjectMap<SSItem> items = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    private final ItemsLoader itemsLoader;

    public SSItemManager(String database) {
        this.itemsLoader = new ItemsLoader(database);
    }

    public void loadItemsFromConfig(SiteShop siteShop) {
        items.clear();

        FileConfiguration config = siteShop.getConfig();
        ConfigurationSection section = config.getConfigurationSection("Items");
        if (section == null || section.getKeys(false) == null) {
            return;
        }

        for (String idString : section.getKeys(false)) {
            String patch = "Items." + idString + ".";
            int price = config.getInt(patch + "price");
            int id = Integer.valueOf(idString);
            List<ItemStack> itemStacks = new ArrayList<>();

            ConfigurationSection configurationSection = config.getConfigurationSection(patch + "itemStacks");
            if (configurationSection == null) {
                continue;
            }

            String depend = config.getString(patch + "depend");

            for (String itemString : configurationSection.getKeys(false)) {
                ItemStack itemStack = config.getItemStack(patch + "itemStacks." + itemString);
                if (depend != null) {
                    itemStack = ItemUtil.getBuilder(itemStack)
                            .setLore("§8" + depend)
                            .build();
                }
                itemStacks.add(itemStack);
            }

            items.put(id, new SSItem(id, price, depend, itemStacks));
        }

        new SiteShopCommand(siteShop);
    }

    @Nullable
    public SSItem getItem(int id) {
        return items.get(id);
    }

    public void close() {
        itemsLoader.close();
    }
}
