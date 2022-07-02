package me.nekocloud.vkbot.command.admin;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.sections.MoneySection;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class EconomyCoinsCommand extends VkCommand {

    public EconomyCoinsCommand() {
        super("coin", "coins", "монетки");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("!монетки <ник> <кол-во>");
            return;
        }

        CorePlayer player = NekoCore.getInstance().getOfflinePlayer(args[0]);

        if (player == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не существует!");
            return;
        }

        val lang = Language.DEFAULT;
        val coins = CommonWords.COINS_1.convert(Integer.parseInt(args[1]), lang);

        player.getSection(MoneySection.class).changeMoney(PurchaseType.COINS, Integer.parseInt(args[1]));
        vkBot.printMessage(message.getPeerId(), "Ты выдал игроку " + player.getName() + " " + Integer.parseInt(args[1]) + " " + coins);
    }

}
