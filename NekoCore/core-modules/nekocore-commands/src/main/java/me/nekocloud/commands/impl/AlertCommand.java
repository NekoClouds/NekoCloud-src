package me.nekocloud.commands.impl;

import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class AlertCommand extends CommandExecutor {

    public AlertCommand() {
        super("corealert", "alert", "объявить", "объявление");

        setGroup(Group.ADMIN);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessageLocale("ALERT_HELP");
            return;
        }

        alert(ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args)));
    }

    public void alert(@NotNull String message) {
        message = ("§5§lОБЪЯВЛЕНИЕ §8➾ §f ") + message;

        for (val player : NekoCore.getInstance().getOnlinePlayers())
            player.sendMessage(message);

        log.info(message);
    }

}
