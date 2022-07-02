package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.survival.commons.trade.TradeManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TradeCommand extends CommonsCommand {

    private final TradeManager tradeManager;

    public TradeCommand(CommonsSurvival commonsSurvival) {
        super(CommonsSurvival.getConfigData(), true, "trade", "обмен");
        tradeManager = new TradeManager(commonsSurvival);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();
        CraftUser craftUser = (CraftUser) USER_MANAGER.getUser(sender);
        if (craftUser == null)
            return;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX", "TRADE_FORMAT");
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            COMMANDS_API.showHelp(gamerEntity, "/trade", "TRADE_HELP");
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                COMMANDS_API.showHelp(gamerEntity, "/trade", "TRADE_HELP");
                return;
            }
            if (craftUser.getTradeReguests().size() == 0) {
                gamer.sendMessageLocale("TRADE_ERROR");
                return;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, args[1]);
                return;
            }
            if (sender == target) {
                gamerEntity.sendMessageLocale("TRADE_ERROR_YOU");
                return;
            }

            tradeManager.acceptRequest(sender, target);
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            COMMANDS_API.playerOffline(gamerEntity, args[0]);
            return;
        }
        if (sender == target) {
            gamerEntity.sendMessageLocale("TRADE_ERROR_YOU");
            return;
        }
        tradeManager.sendRequest(sender, target);
    }
}
