package me.nekocloud.core.discord.utils;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;

public class EmbedUtil {

    public static EmbedBuilder embedModel(MessageReceivedEvent messageReceivedEvent) {
        val embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(messageReceivedEvent.getAuthor().getName(), null, messageReceivedEvent.getAuthor().getAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());
        return embedBuilder;
    }

    public static EmbedBuilder errorMessage(MessageReceivedEvent messageReceivedEvent, String errorType, String errorMessage) {
        val embedBuilder = embedModel(messageReceivedEvent);
        embedBuilder.setTitle(errorType + " Ошибка");
        embedBuilder.setDescription(errorMessage);
        embedBuilder.setColor(0xff0000);

        return embedBuilder;
    }
}
