package me.nekocloud.market.auction.gui;

import me.nekocloud.base.locale.Language;
import me.nekocloud.market.auction.AuctionItemType;
import me.nekocloud.market.auction.AuctionManager;
import me.nekocloud.market.utils.MarketUtil;

public class AuctionMainGui extends AuctionAbstractGui {

    private int amount;

    public AuctionMainGui(AuctionManager manager, Language lang) {
        super(manager, lang, lang.getMessage("AUCTION_MAINGUI_NAME"));

        setItems();
    }

    @Override
    protected void setItems() {
        amount = MarketUtil.setItems(manager, inventory, amount,
                                    manager.getAllItems().values(), lang,
                                    AuctionItemType.ALL, true);
    }
}
