package me.nekocloud.core.discord.command.account;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

// Её отключение делать не будем ибо нехуй.
// Мы заботимся о безопастности игроков
public class TwoFactorModeCommand extends DiscordCommand {

    public TwoFactorModeCommand() {
        super("2fa", "двух-этапка");

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length < 1) {
            inputMessage.reply("❗ Использование: /2fa <vk|discord> \n - Переключает режим двух-факторной авторизации.").queue();
            return;
        }

        val corePlayer = NekoCore.getInstance().getOfflinePlayer(user.getPrimaryAccountName());
        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(corePlayer.getPlayerID());

        if (args[0].equals("vk")) {
            if (authPlayer.getAuthType().equals(0)) {
                inputMessage.reply("❗ Ошибка, у тебя уже включена двух-этапная авторизация по VK").queue();
                return;
            }
            authPlayer.setAuthType(0);
            inputMessage.reply("❗ Ты успешно включил двух-этапную авторизацию по VK").queue();
        } else if (args[0].equals("discord")) {
            if (authPlayer.getAuthType().equals(1)) {
                inputMessage.reply("❗ Ошибка, у тебя уже включена двух-этапная авторизация по Discord").queue();
                return;
            }
            authPlayer.setAuthType(1);
            inputMessage.reply("❗ Ты успешно включил двух-этапную авторизацию по Discord").queue();
        }

    }

}
