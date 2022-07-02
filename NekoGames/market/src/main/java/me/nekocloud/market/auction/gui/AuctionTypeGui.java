package me.nekocloud.market.auction.gui;

import me.nekocloud.base.locale.Language;
import me.nekocloud.market.auction.AuctionItemType;
import me.nekocloud.market.auction.AuctionManager;
import me.nekocloud.market.utils.MarketUtil;

import java.util.stream.Collectors;

public class AuctionTypeGui extends AuctionAbstractGui {

    private final AuctionItemType type;

    private int amount;

    public AuctionTypeGui(AuctionManager manager, Language lang, AuctionItemType type) {
        super(manager, lang, lang.getMessage("AUCTION_MAINGUI_NAME"));
        this.type = type;

        setItems();
    }

    @Override
    protected void setItems() {
        if (type == null)
            return;

        amount = MarketUtil.setItems(manager, inventory, amount, manager.getAllItems().values()
                .stream()
                .filter(auctionItem -> auctionItem.getType() == type)
                .collect(Collectors.toList()), lang, type, true);

        MarketUtil.setBack(manager, inventory, lang);
    }
}
