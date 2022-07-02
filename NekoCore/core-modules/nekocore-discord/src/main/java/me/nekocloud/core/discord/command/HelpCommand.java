package me.nekocloud.core.discord.command;

import lombok.val;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends DiscordCommand{

    public HelpCommand() {
        super("help", "помощь");
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        val stringBuilder = new StringBuilder()
                .append("""
                        ❗ Список доступных команд:\s
                        \s
                        ⚠ Узнать информацию об аккаунте - /инфо\s
                        ⚠ Узнать ник пользователя - /ник\s
                        \s
                        \uD83D\uDCCC Привязать аккаунт - /привязать <ник> <пароль>\s
                        \uD83D\uDCCC Отвязать аккаунт - /отвязать <ник>\s
                        \uD83D\uDCCC Сменить/восстановить пароль - /восстановить\s
                        \s
                        \uD83D\uDC40 Узнать онлайн общего или определённого сервера - /онлайн <сервер>\s
                        \uD83D\uDC40 Найти игрока на сервере - /найти <ник>""");

        inputMessage.reply(stringBuilder).queue();
    }
}
