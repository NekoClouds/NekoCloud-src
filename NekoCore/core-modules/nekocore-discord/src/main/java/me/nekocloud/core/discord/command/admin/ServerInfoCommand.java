package me.nekocloud.core.discord.command.admin;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.IServerManager;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class ServerInfoCommand extends DiscordCommand {

    public ServerInfoCommand() {
        super("сервер", "serverinfo", "server");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        IServerManager serverManager = NekoCore.getInstance().getServerManager();
        Bukkit abstractServer = serverManager.getBukkit(args[0]);

        if (abstractServer == null) {
            inputMessage.reply("Ошибка, данный сервер не существует или не подключен к Core!").queue();
            return;
        }

        String msg =
                "❗ Информация о сервере:" + "\n" +
                "- Название: " + abstractServer.getName() + "\n" +
                "- MOTD: " + abstractServer.getName() + "\n" +
                "- Директория: Bukkit\n" +
                "- Версия ядра: nope" + "\n" +
                "- Количество игроков: " + abstractServer.getOnline() + "\n";

        inputMessage.reply(msg).queue();
    }
}
