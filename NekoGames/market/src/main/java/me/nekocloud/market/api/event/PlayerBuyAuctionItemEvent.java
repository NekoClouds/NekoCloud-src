package me.nekocloud.market.api.event;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.event.player.PlayerEvent;
import me.nekocloud.market.api.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class PlayerBuyAuctionItemEvent extends PlayerEvent implements Cancellable {

    private final AuctionItem auctionItem;

    @Setter
    private boolean cancelled;

    public PlayerBuyAuctionItemEvent(Player player, AuctionItem auctionItem) {
        super(player);
        this.auctionItem = auctionItem;
    }
}
