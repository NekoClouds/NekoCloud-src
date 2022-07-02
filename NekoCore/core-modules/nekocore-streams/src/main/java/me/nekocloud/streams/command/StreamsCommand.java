package me.nekocloud.streams.command;

import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.streams.inventory.StreamsInventory;
import org.jetbrains.annotations.NotNull;

public class StreamsCommand extends CommandExecutor {

    public StreamsCommand() {
        super("streams", "streamlist", "streamslist", "liststream", "liststreams");

        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        new StreamsInventory().openInventory((CorePlayer) sender);
    }

}
