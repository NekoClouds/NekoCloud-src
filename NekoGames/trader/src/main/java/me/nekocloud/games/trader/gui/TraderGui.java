package me.nekocloud.games.trader.gui;

import lombok.val;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.games.trader.Trader;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.nekocloud.games.trader.event.TraderSellEvent;
import me.nekocloud.games.trader.utils.TraderUtil;

import java.util.Map;

public class TraderGui extends AbstractGui<DInventory> {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    public TraderGui(Player player) {
        super(player);

        createInventory();
    }

    @Override
    protected void createInventory() {
        lang = gamer.getLanguage();
        inventory = INVENTORY_API.createInventory(lang.getMessage("TRADER_GUI_NAME"), 4);
    }

    @Override
    protected void setItems() {
        val marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getPlayerID());
        int slot = 10;
        for (val entry : Trader.getInstance().getTraderManager().getCurrentItems().entrySet()) {
            inventory.setItem(slot, new DItem(entry.getKey(), (player1, clickType, i) -> {
                ItemStack item = entry.getKey();
                int amount = TraderUtil.getItemsAmount(player1, item);
                if (amount == 0) {
                    gamer.sendMessageLocale("TRADER_NO_ITEMS");
                    return;
                }

                if (amount < item.getAmount()) {
                    gamer.sendMessageLocale("TRADER_NOT_ENOUGH_ITEMS");
                    return;
                }

                TraderUtil.removeItems(player1, item, item.getAmount());
                marketPlayer.changeMoney(+entry.getValue());
                gamer.sendMessageLocale("TRADER_SELL_ITEMS", item.getAmount(), entry.getValue());
                BukkitUtil.callEvent(new TraderSellEvent(gamer, item, item.getAmount(), entry.getValue()));
            }));

            if (slot == 16) {
                slot = 19;
            } else {
                slot++;
            }
        }
    }
}
