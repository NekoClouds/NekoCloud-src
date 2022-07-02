package me.nekocloud.vkbot.command.account;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class TwoFactorCallbackCommand extends VkCommand {

    private final boolean allowLogin;

    public TwoFactorCallbackCommand(boolean allowLogin) {
        super( "2fa" + (allowLogin ? "_accept" : "_deny"));
        this.allowLogin = allowLogin;

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        val manager = CoreAuth.getAuthManager();
//        if (!manager.hasTwofactorSession(vkUser.getPrimaryAccountName())) {
//            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, сессия была преждевременно завершена!");
//            return;
//        }

        val player = NekoCore.getInstance().getPlayer(args[0]);
       // manager.removeTwofactorSession(args[0]);

        if (player == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Упс... Аккаунт уже не в сети :(");
            return;
        }

        if (allowLogin) {
            player.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fВход был успешно подтвержден через VK!\n§d§lАВТОРИЗАЦИЯ §8| §fПриятной игры!");
            vkBot.printMessage(message.getPeerId(), "❗ Вход успешно выполнен, приятной игры!");

            manager.loadOrCreate(player.getPlayerID()).complete();
            return;
        }

        player.disconnect(new TextComponent("§d§lNeko§f§lCloud \n\n §dВаша сессия была завершена через VK"));
        vkBot.printMessage(message.getPeerId(), "Сессия входа была завершена! Советуем вам сменить пароль от аккаунта. \n Для этого используйте команду !восстановить");
    }

}
