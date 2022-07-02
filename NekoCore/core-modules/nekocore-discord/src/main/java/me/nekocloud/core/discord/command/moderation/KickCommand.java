package me.nekocloud.core.discord.command.moderation;

import lombok.val;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.api.handler.MessageHandler;
import me.nekocloud.core.discord.user.DiscordUser;
import me.nekocloud.core.discord.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends DiscordCommand {

    public KickCommand() {
        super("kick");
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (inputMessage.getMentionedUsers().isEmpty()) throw new IllegalArgumentException("Не указан пользователь!");

        Member target = inputMessage.getMentionedMembers().get(0);

        String reason = args.length <= 1 ? "Не указана причина кика! " : String.join(" ", args).substring(target.getAsMention().length() + 1);

        inputMessage.getGuild().kick(target, reason).queue();

        val embedBuilder = EmbedUtil.embedModel(MessageHandler.getEvent());
        embedBuilder.setTitle(String.format("%s был исключен %s", target.getUser().getName(), inputMessage.getAuthor().getName()));
        embedBuilder.setDescription("Причина: " + reason);
        embedBuilder.setColor(0x00ee99);

        inputMessage.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
