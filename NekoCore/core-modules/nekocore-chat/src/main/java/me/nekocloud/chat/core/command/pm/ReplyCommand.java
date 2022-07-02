package me.nekocloud.chat.core.command.pm;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.chat.core.util.PrivateMessageUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand extends CommandExecutor {

    public ReplyCommand() {
        super("reply", "r", "ответ", "ответить");

        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            notEnoughArguments(sender, "CHAT_PREFIX", "REPLY_FORMAT");
            return;
        }

        val player = (CorePlayer) sender;
        val message = ChatColor.stripColor(Joiner.on(" ").join(args));
        val target = NekoCore.getInstance().getPlayer(Integer.parseInt(player.getData("reply")));
        if (target == null) {
            player.sendMessageLocale("NO_PLAYERS_TO_REPLY");
            return;
        }

        PrivateMessageUtil.replyMessage(player, message);
    }

}
