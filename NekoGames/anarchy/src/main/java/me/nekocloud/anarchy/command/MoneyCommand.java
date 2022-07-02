package me.nekocloud.anarchy.command;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;

public class MoneyCommand implements CommandInterface {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    public MoneyCommand() {
        SpigotCommand command = COMMANDS_API.register("money", this, "деньги", "balance");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getName());
        if (marketPlayer == null)
            return;

        double money = marketPlayer.getMoney();
        String end = CommonWords.COINS_1.convert((int) money, gamerEntity.getLanguage());
        gamerEntity.sendMessageLocale("BALANCE", StringUtil.getNumberFormat(money), end);
    }
}
