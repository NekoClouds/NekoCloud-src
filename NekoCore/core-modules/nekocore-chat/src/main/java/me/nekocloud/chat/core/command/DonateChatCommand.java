package me.nekocloud.chat.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class DonateChatCommand extends CommandExecutor {

    public DonateChatCommand() {
        super("dc", "donatechat", "донч", "дончат");

        setGroup(Group.HEGENT);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCooldown(10, "donate_chat");
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            notEnoughArguments(sender, "CHAT_PREFIX", "DONATE_CHAT_FORMAT");
            return;
        }

        if (!player.getSetting(SettingsType.DONATE_CHAT)) {
            player.sendMessageLocale("DONATE_CHAT_OFF");
            return;
        }

        val donateMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));

        for (val donatePlayer : NekoCore.getInstance().getOnlinePlayers(corePlayer -> !(corePlayer.getGroup().getLevel() < Group.DEFAULT.getLevel())
                && corePlayer.getSetting(SettingsType.DONATE_CHAT))) {
            donatePlayer.sendMessage("§3§lДОНАТ-ЧАТ §8| §r" + player.getDisplayName() + " §8➾ §f" + donateMessage);
        }
    }

}
