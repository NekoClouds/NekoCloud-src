package me.nekocloud.reports.command;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReportListCommand extends CommandExecutor {
    public ReportListCommand() {
        super("reports", "reportlist", "жалобы", "rs");
        setGroup(Group.JUNIOR);
        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
       // (new ReportInventory()).openInventory((CorePlayer) sender);
    }
}
