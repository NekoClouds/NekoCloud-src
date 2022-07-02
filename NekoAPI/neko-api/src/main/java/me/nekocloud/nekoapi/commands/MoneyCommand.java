package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;

public final class MoneyCommand implements CommandInterface {

    public MoneyCommand() {
        val spigotCommand = COMMANDS_API.register("money", this,
                "balance", "bal", "баланс");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(
            final GamerEntity gamerEntity,
            final String command,
            final String[] args
    ) {
        final BukkitGamer gamer = (BukkitGamer) gamerEntity;
        final Language lang = gamer.getLanguage();

        final int coins = gamer.getMoney(PurchaseType.COINS);
        final int virts = gamer.getMoney(PurchaseType.VIRTS);
        gamer.sendMessageLocale("BALANCE",
                "§6" + StringUtil.getNumberFormat(coins) + "§f " + CommonWords.COINS_1.convert(coins, lang) +
                        "§d" + StringUtil.getNumberFormat(virts) + "§f " + CommonWords.VIRTS_1.convert(virts, lang));
    }
}