package me.nekocloud.core.discord.api.handler;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.discord.bot.DiscordBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

@Log4j2
public class MessageHandler extends ListenerAdapter { // #КодНовита

    @Getter
    private static MessageReceivedEvent event;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageHandler.event = event;
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        DiscordBot.COMMAND_MAP.forEach((msg, command) -> {
            if (event.getMessage().getContentDisplay().toLowerCase().startsWith("/" + msg)
                || event.getMessage().getContentDisplay().toLowerCase().startsWith("!" + msg)) {

                // форматируем сообщение
                val formatted = event.getMessage().getContentRaw().substring(1).split(" ");
                // форматируем аргументы
                val args = Arrays.copyOfRange(formatted, 1, formatted.length);

                command.executeCommand(
                        args,
                        event.getAuthor(),
                        event.getMessage(),
                        event.getChannel());

                log.debug("[DiscordBot] Success executed command /"+ msg +" (" + event.getAuthor().getAsTag() + ")");
            }
        });
    }
}
