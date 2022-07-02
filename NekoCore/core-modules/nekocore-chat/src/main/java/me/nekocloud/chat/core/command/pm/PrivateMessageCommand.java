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

import java.util.Arrays;

public class PrivateMessageCommand extends CommandExecutor {

    public PrivateMessageCommand() {
        super("message",  "msg", "pm", "postmessage",
                "pmsg", "смс", "sms", "pmessage", "лс", "сообщение", "bmsg", "w", "tell",
                "tellraw", "ьып");
        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        val player = (CorePlayer) sender;

        if (args.length < 2) {
            notEnoughArguments(sender, "CHAT_PREFIX", "MSG_FORMAT");
            return;
        }

        val targetPlayer = NekoCore.getInstance().getPlayer(args[0]);
        if (targetPlayer == null) {
            playerOffline(sender, args[0]);
            return;
        }

        val message = ChatColor.stripColor(Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)));
        PrivateMessageUtil.sendMessage(player, targetPlayer, message);
    }
}
