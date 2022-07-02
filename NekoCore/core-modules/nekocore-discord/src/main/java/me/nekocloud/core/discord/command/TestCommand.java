package me.nekocloud.core.discord.command;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class TestCommand extends DiscordCommand {

    public TestCommand() {
        super("test");
    }

    @Override
    @SneakyThrows
    public void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        inputMessage.reply(" " + args.length).queue();

        channel.sendMessage("test").queue(message -> {
            message.addReaction(":white_check_mark:").queue();
            message.addReaction(":x:").queue();
        });
    }
}
