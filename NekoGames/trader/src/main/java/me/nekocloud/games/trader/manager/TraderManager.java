package me.nekocloud.games.trader.manager;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.CollectionUtil;
import me.nekocloud.games.trader.Trader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TraderManager implements Runnable {

    Map<ItemStack, Integer> items = new ConcurrentHashMap<>();
    Map<ItemStack, Integer> currentItems = new ConcurrentHashMap<>();

    public void load(Trader trader) {
        val config = trader.getConfig();
        config.getConfigurationSection("items").getValues(true).forEach((key, value) -> {
            val amount = Integer.parseInt(((String) value).split(" ")[1]);
            val price = Integer.parseInt(((String) value).split(" ")[0]);
            items.put(ItemUtil.getBuilder(Material.getMaterial(key))
                    .setLore(Language.DEFAULT.getList("TRADER_ITEM_LORE", price, amount))
                    .setAmount(amount)
                    .build(), price);
        });
    }

    @Override
    public void run() {
        currentItems.clear();
        int size = 0;

        for (val entry : CollectionUtil.shuffle(items).entrySet()) {
            if (size == 14 || size > items.size())
                break;

            currentItems.put(entry.getKey(), entry.getValue());
            size++;
        }
    }

    public Map<ItemStack, Integer> getItems() {
        return ImmutableMap.copyOf(items);
    }
}
