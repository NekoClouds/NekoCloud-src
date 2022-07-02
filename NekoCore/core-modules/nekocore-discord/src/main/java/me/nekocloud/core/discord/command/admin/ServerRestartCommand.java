package me.nekocloud.core.discord.command.admin;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class ServerRestartCommand extends DiscordCommand {

    public ServerRestartCommand() {
        super("serverrestart", "reloadserver", "рестарт", "serverstop", "stopserver", "stop");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply("❗ Ошибка, используй: !рестарт <имя сервера>").queue();
            return;
        }

        Bukkit abstractServer = NekoCore.getInstance().getBukkit(args[0]);

        if (abstractServer == null) {
            inputMessage.reply("❗ Ошибка, данный сервер не существует или не подключен к Core!").queue();
            return;
        }

        abstractServer.restart();
        inputMessage.reply("Сервер " + abstractServer.getName() + " был отправлен нахуй!").queue();
    }

}
