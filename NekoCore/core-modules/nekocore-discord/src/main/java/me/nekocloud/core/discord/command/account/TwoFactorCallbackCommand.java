package me.nekocloud.core.discord.command.account;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class TwoFactorCallbackCommand extends DiscordCommand {

    private final boolean allowLogin;

    public TwoFactorCallbackCommand(boolean allowLogin) {
        super( "2fa" + (allowLogin ? "_accept" : "_deny"));
        this.allowLogin = allowLogin;

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
//        if (!CoreAuth.getAuthManager().hasTwofactorSession(user.getPrimaryAccountName())) {
//            inputMessage.reply("❗ Ошибка, сессия была преждевременно завершена!").queue();
//            return;
//        }

        val player = NekoCore.getInstance().getPlayer(args[0]);
        //CoreAuth.getAuthManager().removeTwofactorSession(args[0]);

        if (player == null) {
            inputMessage.reply( "❗ Упс... Аккаунт уже не в сети :(").queue();
            return;
        }

        if (allowLogin) {
            player.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fВход был успешно подтвержден через VK!\n§d§lАВТОРИЗАЦИЯ §8| §fПриятной игры!");
            channel.sendMessage("❗ Вход успешно выполнен, приятной игры!").queue();

            CoreAuth.getAuthManager().loadOrCreate(player.getPlayerID()).complete();
            return;
        }

        player.disconnect(new TextComponent("§d§lNeko§f§lCloud \n\n §dВаша сессия была завершена через VK"));
        channel.sendMessage("Сессия входа была завершена! Советуем вам сменить пароль от аккаунта. \n Для этого используйте команду !восстановить").queue();

    }

}
