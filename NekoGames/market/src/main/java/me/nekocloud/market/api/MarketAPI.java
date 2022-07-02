package me.nekocloud.market.api;

import me.nekocloud.market.player.CraftMarketManager;

public final class MarketAPI {

    private static MarketPlayerManager marketPlayerManager;

    /**
     * интерфейс для работы с игроками магазина
     * @return - интерфейс
     */
    public static MarketPlayerManager getMarketPlayerManager() {
        if (marketPlayerManager == null)
            marketPlayerManager = new CraftMarketManager();

        return marketPlayerManager;
    }
}
