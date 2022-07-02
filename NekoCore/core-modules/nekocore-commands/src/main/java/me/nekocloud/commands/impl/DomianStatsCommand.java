package me.nekocloud.commands.impl;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DomianStatsCommand extends CommandExecutor {

    public DomianStatsCommand() {
        super("clearchat", "очиститьчат", "чаточистить");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setGroup(Group.AXSIDE);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage("§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n§7\n");
        sender.sendMessage("§d§lNeko§f§lCloud §8| §fЧат был успешно очищен");
    }
}