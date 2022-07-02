package me.nekocloud.games.trader.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.inventory.ItemStack;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TraderSellEvent extends GamerEvent {

    ItemStack item;
    int amount;
    int price;

    public TraderSellEvent(BukkitGamer gamer, ItemStack item, int amount, int price) {
        super(gamer);

        this.item = item;
        this.amount = amount;
        this.price = price;
    }
}
