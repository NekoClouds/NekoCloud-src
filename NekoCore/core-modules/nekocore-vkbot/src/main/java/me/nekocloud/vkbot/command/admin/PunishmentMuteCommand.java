package me.nekocloud.vkbot.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.IServerManager;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PunishmentMuteCommand extends VkCommand {

    public PunishmentMuteCommand() {
        super("мут", "mute", "замутить");

        setGroup(Group.JUNIOR);
        setShouldLinkAccount(true);
    }

    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {

        String playerName = vkUser.getPrimaryAccountName();
        CorePlayer player = NekoCore.getInstance().getOfflinePlayer(playerName);
        Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(playerName);

        if (args.length < 3) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте !мут <ник> <время> <причина>");
            vkBot.printMessage(message.getPeerId(), "- Например: !мут Игрок 10m Пиар сервера");
            return;
        }
        if (!player.isStaff()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, вам нужен статус " + Group.JUNIOR.getName() + " и выше!");
            return;
        }
        CorePlayer targetPlayer = NekoCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок хз где!");
            return;
        }
        if (targetPlayer.getGroup().getLevel() >= player.getGroup().getLevel() && player.isAdmin()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, вы не можете замутить данного игрока тк выше или равен по правам :(");
            return;
        }
        String muteReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 2, args.length)));
        String muteTime = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 1, args.length)));
        vkBot.printMessage(message.getPeerId(), "✅ Вы успешно замутили " + ChatColor.stripColor(targetPlayer.getDisplayName()) + " с причиной: " + ChatColor.stripColor(muteReason));
        mutePlayer(player, targetPlayer, muteTime, muteReason);

        for (CorePlayer staffCorePlayer : NekoCore.getInstance().getOnlinePlayers(IBaseGamer::isStaff))
            staffCorePlayer.sendMessage("§3§lМодерация §8| " + player.getDisplayName() + " §7замутил игрока " + targetPlayer.getDisplayName() + " §7через §9VK §7на " + muteTime + " §7по причине: " + muteReason);
    }

    public void mutePlayer(@NotNull CorePlayer ownerPlayer, @NotNull CorePlayer intruderPlayer, @NotNull String muteTime, @NotNull String muteReason) {
        IServerManager serverManager = NekoCore.getInstance().getServerManager();
        Bukkit abstractServer = serverManager.getServersByPrefix("Hub").stream().findAny().get();

        abstractServer.sendCommand("mute " + intruderPlayer.getName() + " " + muteTime + "  " + muteReason);
    }
}