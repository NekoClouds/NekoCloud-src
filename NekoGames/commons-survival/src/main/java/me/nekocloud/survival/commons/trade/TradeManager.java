package me.nekocloud.survival.commons.trade;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TradeManager implements Listener {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();
    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();

    private final List<Trade> trades = new ArrayList<>();

    public TradeManager(CommonsSurvival commonsSurvival) {
        Bukkit.getPluginManager().registerEvents(this, commonsSurvival);
    }

    public void sendRequest(Player who, Player target) {
        BukkitGamer gamer = gamerManager.getGamer(who);
        BukkitGamer targetGamer = gamerManager.getGamer(target);
        if (gamer == null || targetGamer == null)
            return;
        CraftUser targetUser = (CraftUser) userManager.getUser(target);
        if (target == null)
            return;

        if (!targetUser.addTradeRequest(who)) {
            gamer.sendMessageLocale("CALL_ERROR");
            return;
        }

        Language lang = targetGamer.getLanguage();


        //todo сообщение о том, что вам отправили /trade (отправить target)
        //todo заявка активна 120 сек только
    }

    public void acceptRequest(Player who, Player target) {
        BukkitGamer gamer = gamerManager.getGamer(who);
        BukkitGamer targetGamer = gamerManager.getGamer(target);
        if (gamer == null || targetGamer == null)
            return;
        CraftUser targetUser = (CraftUser) userManager.getUser(target);
        if (target == null)
            return;
        if (targetUser.getTradeReguests().remove(who.getName()) == null) {
            gamer.sendMessageLocale("TRADE_ERROR2", target.getDisplayName());
            return;
        }
        Trade trade = new Trade(who, target, gamer.getLanguage(), targetGamer.getLanguage());
        if (!trade.isInit()) {
            gamer.sendMessageLocale("TRADE_BROKEN");
            targetGamer.sendMessageLocale("TRADE_BROKEN");
            return;
        }

        trades.add(trade);
    }

    //todo эвенты для работы гуи
}
