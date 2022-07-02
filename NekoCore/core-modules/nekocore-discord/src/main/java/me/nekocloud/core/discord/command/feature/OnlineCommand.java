package me.nekocloud.core.discord.command.feature;

import lombok.val;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class OnlineCommand extends DiscordCommand {

    public OnlineCommand() {
        super("онлайн", "online");
    }

    @Override
    public void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply("Общий онлайн сервера - " +
                    NumberUtils.spaced(NekoCore.getInstance().getOnline())).queue();
        }

        val serverManager = NekoCore.getInstance().getServerManager();

        if (args[0].startsWith("@")) {
            String serverPrefix = args[0].substring(1);

            inputMessage.reply("Онлайн серверов по префиксу " + serverPrefix.toUpperCase() + " - "
                    + NumberUtils.spaced(NekoCore.getInstance().getOnlineByServerPrefix(serverPrefix))).queue();
            return;
        }

        val abstractServer = serverManager.getBukkit(args[0]);

        if (abstractServer == null) {
            inputMessage.reply("❗ Ошибка, данный сервер не существует или не подключен к Core!").queue();
            return;
        }

        inputMessage.reply("Онлайн сервера " + abstractServer.getName() + " - " + NumberUtils.spaced(abstractServer.getOnline())).queue();
    }
}
