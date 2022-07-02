package me.nekocloud.chat.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class StaffChatCommand extends CommandExecutor {

    public StaffChatCommand() {
        super("sc", "staffchat");

        setGroup(Group.JUNIOR);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            notEnoughArguments(sender, "CHAT_PREFIX", "STAFF_CHAT_FORMAT");
            return;
        }

        val staffMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));

        for (val staffPlayer : NekoCore.getInstance().getOnlinePlayers(corePlayer -> corePlayer.getGroup().getLevel() >= Group.JUNIOR.getLevel())) {
            staffPlayer.sendMessage("§6§lСТАФФ-ЧАТ §8| §r" + player.getDisplayName() + " §8➾ §f" + staffMessage);
        }
    }

}
