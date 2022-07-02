package me.nekocloud.market.command;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;

public final class ConvertCommand implements CommandInterface {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    public ConvertCommand() {
        SpigotCommand command = COMMANDS_API.register("convert", this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "CONVERT_FORMAT");
            return;
        }

        int sum;
        try {
            sum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            gamer.sendMessageLocale("CONVERT_ERROR");
            return;
        }

        if (sum < 1) {
            gamer.sendMessageLocale("CONVERT_ERROR");
            return;
        }

        if (!gamer.changeMoney(PurchaseType.VIRTS, -sum)) {
            return;
        }

        MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getPlayerID());
        if (marketPlayer == null) {
            return;
        }

        int total = sum * 300;  //за 1 вирт даем 300 монет на сурвачах
        marketPlayer.changeMoney(total);
        gamer.sendMessageLocale("CONVERT",
                StringUtil.getNumberFormat(sum),
                StringUtil.getNumberFormat(total));
    }
}
