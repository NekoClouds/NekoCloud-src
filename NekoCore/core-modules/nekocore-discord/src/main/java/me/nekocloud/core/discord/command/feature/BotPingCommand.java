package me.nekocloud.core.discord.command.feature;

import lombok.val;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.api.handler.MessageHandler;
import me.nekocloud.core.discord.user.DiscordUser;
import me.nekocloud.core.discord.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class BotPingCommand extends DiscordCommand {

    public BotPingCommand() {
        super("ping");
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        val start = System.currentTimeMillis();
        inputMessage.reply("ТЕСТИРОВАНИЕ БОТА").queue(message -> {

            val end = System.currentTimeMillis();
            val apiLatency = end - start;

            val pingEmbed = EmbedUtil.embedModel(MessageHandler.getEvent());
            pingEmbed.addField(":alarm_clock: API Задержка", channel.getJDA().getGatewayPing() + " ms", true);
            pingEmbed.addField(":robot: Задержка клиента", apiLatency + " ms", true);


            int color;
            if (apiLatency < 100) {
                color = 0x00ff00;
            } else if (apiLatency < 200) {
                color = 0xffff00;
            } else {
                color = 0xff0000;
            }
            pingEmbed.setColor(color);

            message.editMessageEmbeds(pingEmbed.build()).queue();
        });
    }
}
