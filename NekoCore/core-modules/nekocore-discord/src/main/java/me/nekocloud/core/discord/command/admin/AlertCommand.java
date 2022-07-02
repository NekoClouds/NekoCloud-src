package me.nekocloud.core.discord.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.sender.VkbotCommandSender;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class AlertCommand extends DiscordCommand {

    public AlertCommand() {
        super("alert", "объявить", "объявление");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply("❗ Ошибка, используй: !alert <сообщение>").queue();
            return;
        }

        String alertMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));
        NekoCore.getInstance().getCommandManager().dispatchCommand(VkbotCommandSender.getInstance(), "alert " + alertMessage);

        inputMessage.reply("✅ На все сервера отправлено сообщение:\n" +
                " ОБЪЯВЛЕНИЕ | " + ChatColor.stripColor(alertMessage)).queue();
    }

}
