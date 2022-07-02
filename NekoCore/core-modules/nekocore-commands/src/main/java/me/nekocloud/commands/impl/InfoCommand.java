package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.command.sender.ConsoleCommandSender;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends CommandExecutor {

    public InfoCommand() {
        super("playerinfo","whois");

        setGroup(Group.AXSIDE);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        CorePlayer player;

        if (args.length == 0) {

            // Это если консоль пишет
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§cОшибка, пишите - /playerinfo <ник игрока>");
                return;
            }

            player = (CorePlayer) sender;

        } else {
            player = NekoCore.getInstance().getOfflinePlayer(args[0]);
        }

        if (player == null) {
            playerNeverPlayed(sender, args[0]);
            return;
        }

        val playerID = player.getPlayerID();
        val lang = sender.getLanguage();

        sender.sendMessagesLocale("PLAYER_INFO",
                player.getDisplayName(),
                playerID,
                player.getGroup().getNameEn(),

                player.getMoney(PurchaseType.COINS),
                player.getMoney(PurchaseType.VIRTS),

                player.getLanguage().getName(),

                NumberUtils.spaced(player.getLevelNetwork()),
                NumberUtils.spaced(player.getExp()),
                NumberUtils.spaced(player.getExpNextLevel())); //todo

        if (player.isOnline()) {
            sender.sendMessagesLocale("PLAYER_INFO_ONLINE",
                    lang.getMessage("ONLINE"),
                    player.getBukkit().getName(),
                    player.getBungee().getName(),
                    player.getVersion().toClientName());

            JsonChatMessage.create(" §7[ Узнать PING ]")
                    .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §7узнать пинг игрока")
                    .addClick(ClickEvent.Action.RUN_COMMAND, "/ping " + player.getName())

                    .sendMessage(sender);

            if (sender.getGroup().getId() >= 700) {
                int vkId = CoreAuth.getAuthManager().loadOrCreate(playerID).getVkId();

                if (vkId > 0) {
                    JsonChatMessage.create(" §2[ Открыть VK ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §fоткрыть ссылку на §9VK §fигрока")
                            .addClick(ClickEvent.Action.OPEN_URL, "https://vk.com/id" + vkId)

                            .sendMessage(sender);
                }

                JsonChatMessage.create(" §a[ Замутить ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §aзамутить игрока")
                        .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/mute " + player.getName() + " ")

                        .sendMessage(sender);

                JsonChatMessage.create(" §e[ Кикнуть ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §eкикнуть игрока")
                        .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/kick " + player.getName() + " ")

                        .sendMessage(sender);

                JsonChatMessage.create(" §c[ Заблокировать ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §cзаблокировать игрока")
                        .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/ban " + player.getName() + " ")

                        .sendMessage(sender);

                if (player.isAdmin()) {
                    JsonChatMessage.create(" §9[ Узнать IP ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §6узнать IP игрока")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/айпи " + player.getName())

                            .sendMessage(sender);

                    JsonChatMessage.create(" §4[ Крашнуть клиент ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §4выключить клиент игрока")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/crash " + player.getName())

                            .sendMessage(sender);
                }
            }

        } else {
            sender.sendMessagesLocale("PLAYER_INFO_OFFLINE",
                    lang.getMessage("OFFLINE"),
                    player.getOfflineData().getLastServerName(),
                    TimeUtil.leftTime(sender.getLanguage(), System.currentTimeMillis() - player.getOfflineData().getLastOnline()));

            int vkId = CoreAuth.getAuthManager().loadOrCreate(playerID).getVkId();

            if (vkId > 0) {
                JsonChatMessage.create(" §9[ Открыть VK ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "§d↪ §7Кликни, чтобы §fоткрыть ссылку на §9VK §fигрока")
                        .addClick(ClickEvent.Action.OPEN_URL, "https://vk.com/id" + vkId)

                        .sendMessage(sender);
            }
        }
    }
}