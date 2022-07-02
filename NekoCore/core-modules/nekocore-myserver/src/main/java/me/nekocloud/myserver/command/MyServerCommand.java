package me.nekocloud.myserver.command;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.ValidateUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.myserver.inventory.ServerManagementInventory;
import me.nekocloud.myserver.type.MyServerCategory;
import me.nekocloud.myserver.type.MyServerManager;
import me.nekocloud.myserver.type.MyServerType;
import me.nekocloud.myserver.type.PlayerMyServer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class MyServerCommand extends CommandExecutor {

    public MyServerCommand() {
        super("myserver", "ms");

        setGroup(Group.AXSIDE);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        val player = (CorePlayer) sender;
        val playerMyServer = MyServerManager.INSTANCE.getPlayerServer(player);

        if (args.length == 0) {

            if (playerMyServer != null) {
                new ServerManagementInventory(playerMyServer).openInventory(player);

            } else {

                sendHelpMessage(sender);
            }
            return;
        }

        switch (args[0].toLowerCase()) {

            case "create": {
                if (playerMyServer != null) {
                    player.sendMessage("§cОшибка, Вы уже создали сервер ранее!");
                    break;
                }

                if (args.length < 2) {
                    player.sendMessage("§cОшибка, пишите - /myserver create <индекс/префикс сервера>");

                    break;
                }

                MyServerType myServerType;

                if (ValidateUtil.isNumber(args[1])) myServerType = MyServerType.of(Integer.parseInt(args[1]));
                else myServerType = MyServerType.of(args[1]);

                if (myServerType == null) {

                    player.sendMessage(ChatColor.RED + "Ошибка, неверно указана категория сервера!");
                    player.sendMessage(ChatColor.RED + "Чтобы узнать, какие категории доступны, то пишите /myserver list");
                    break;
                }

                player.sendMessage("§6§lMyServer §8:: §fНачалось создание сервера...");
                MyServerManager.INSTANCE.createServer(player, myServerType);
                break;
            }

            case "list": {
                player.sendMessage("§6§lMyServer §8:: §fСписок доступных серверов:");

                for (MyServerCategory serverCategory : MyServerCategory.SERVER_CATEGORIES) {
                    player.sendMessage("§e" + serverCategory.getName() + " (ID: " + serverCategory.getTypeIndex() + ")");

                    for (MyServerType serverType : serverCategory.getServerTypes()) {
                        player.sendMessage(" §7" + serverType.getSubTypeIndex() + ". §6" + serverType.getSubName() + " §f(" + serverType.name().toLowerCase() + ")");

                        if (Arrays.stream(Objects.requireNonNull(serverType.getServersFolder().toFile().listFiles())).anyMatch(serverFolder -> !MyServerManager.INSTANCE.isAvailable(serverFolder.toPath()))) {
                            player.sendMessage("  §8- §7Сейчас запущено §e" + MyServerManager.INSTANCE.getActiveServers(serverType).size() + " §7серверов");

                        } else {

                            player.sendMessage("  §8- §cНет доступных серверов");
                        }
                    }
                }

                break;
            }

            case "stats": {
                player.sendMessage("§6§lMyServer §8:: §fСтатистика серверов:");

                Collection<Bukkit> connectedServers = NekoCore.getInstance().getServersByPrefix("ms-");
                Collection<PlayerMyServer> activeServers = MyServerManager.INSTANCE.getActiveServers();

                // Connected servers to Core.
                player.sendMessage(" §f[Подключено к Core]: §7(" + connectedServers.size() + ")");

                if (!connectedServers.isEmpty()) {
                    JsonChatMessage jsonChatMessage = JsonChatMessage.create().addText("  ");

                    int serverCounter = 1;
                    for (Bukkit bukkitServer : connectedServers) {
                        JsonChatMessage serverJson = JsonChatMessage.create();

                        serverJson.addText(ChatColor.YELLOW + bukkitServer.getName());
                        serverJson.addHover(HoverEvent.Action.SHOW_TEXT,
                                "§7Название сервера: §e" + bukkitServer.getName() + "\n" +
                                        "§7Онлайн: §e" + bukkitServer.getOnlineCount() + "\n" +
                                        "§7Версия ядра: §b" + bukkitServer.getVersionName());

                        jsonChatMessage.addComponents(serverJson.build());

                        if (serverCounter < activeServers.size())
                            jsonChatMessage.addText("§f, ");

                        serverCounter++;
                    }

                    player.sendMessage(ChatMessageType.CHAT, jsonChatMessage.build());

                } else {

                    player.sendMessage("  §cСписок серверов пуст!");
                }


                // Active system servers
                player.sendMessage(" §f[Активные сервера]: §7(" + activeServers.size() + ")");

                if (!activeServers.isEmpty()) {
                    JsonChatMessage jsonChatMessage = JsonChatMessage.create().addText("  ");

                    int serverCounter = 1;
                    for (PlayerMyServer myServer : activeServers) {
                        JsonChatMessage serverJson = JsonChatMessage.create();

                        serverJson.addText((myServer.isRunning() ? ChatColor.GREEN : ChatColor.RED) + myServer.getServerName());
                        serverJson.addHover(HoverEvent.Action.SHOW_TEXT,
                                "§7Название: §e" + myServer.getServerName() + "\n" +
                                        "§7Создатель: " + myServer.getOwner().getDisplayName() + "\n" +
                                        "§7Тип: §e" + myServer.getServerType().name().toLowerCase() + "\n" +
                                        (myServer.isRunning() ? "§aСервер запущен и активен!" : "§cСервер отключен!"));

                        jsonChatMessage.addComponents(serverJson.build());

                        if (serverCounter < activeServers.size())
                            jsonChatMessage.addText("§f, ");

                        serverCounter++;
                    }

                    player.sendMessage(ChatMessageType.CHAT, jsonChatMessage.build());

                } else {

                    player.sendMessage("  §cСписок серверов пуст!");
                }

                break;
            }

            default: {
                sendHelpMessage(sender);

                break;
            }
        }

    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage("§6§lMyServer §8| §fСписок доступных команд:");
        sender.sendMessage(" §7Создать свой сервер - §e/myserver create <индекс/префикс категории>");
        sender.sendMessage(" §7Статистика серверов - §e/myserver stats");
        sender.sendMessage(" §7Список доступных серверов - §e/myserver list");
    }

}
