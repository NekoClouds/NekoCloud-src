package me.nekocloud.vkbot.command.admin;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.sections.MoneySection;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.NekoCore;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class EconomyVirtsCommand extends VkCommand {

    public EconomyVirtsCommand() {
        super("virt", "virts", "вирты");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("!вирты <ник> <кол-во>");
            return;
        }
        if (!hasIdentifier(args[0])) return;
        val player = NekoCore.getInstance().getOfflinePlayer(args[0]);
        if (player == null) {
            playerOffline(args[0]);
            return;
        }

        val lang = Language.DEFAULT;
        val virts = CommonWords.VIRTS_1.convert(Integer.parseInt(args[1]), lang);

        player.getSection(MoneySection.class).changeMoney(PurchaseType.VIRTS, Integer.parseInt(args[1]));
        vkBot.printMessage(message.getPeerId(), "Ты выдал игроку " + player.getName() + " " + Integer.parseInt(args[1]) + " " + virts);
    }

}
