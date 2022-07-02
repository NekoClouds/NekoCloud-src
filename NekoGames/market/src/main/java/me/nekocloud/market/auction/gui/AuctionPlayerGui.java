package me.nekocloud.market.auction.gui;

import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.market.api.AuctionItem;
import me.nekocloud.market.auction.AuctionItemType;
import me.nekocloud.market.auction.AuctionManager;
import me.nekocloud.market.utils.MarketUtil;

import java.util.List;
import java.util.stream.Collectors;

public class AuctionPlayerGui extends AuctionAbstractGui {

    private final IBaseGamer gamerWho;

    private int amount;

    public AuctionPlayerGui(AuctionManager manager, Language lang, IBaseGamer gamerWho) {
        super(manager, lang, lang.getMessage("AUCTION_PLAYERGUI_NAME", gamerWho.getName()));
        this.gamerWho = gamerWho;

        setItems();
    }

    @Override
    protected void setItems() {
        if (gamerWho == null)
            return;
        List<AuctionItem> auctionItemList = manager.getAllItems().values()
                .stream()
                .filter(auctionItem -> auctionItem.getOwner().getPlayerID() == gamerWho.getPlayerID())
                .collect(Collectors.toList());

        amount = MarketUtil.setItems(manager, inventory, amount, auctionItemList,
                                    lang, AuctionItemType.ALL, false);

        MarketUtil.setBack(manager, inventory, lang);
    }
}
