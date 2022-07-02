package me.nekocloud.streams.command;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.streams.StreamManager;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import me.nekocloud.streams.platform.StreamPlatform;
import org.jetbrains.annotations.NotNull;

public class StreamCommand extends CommandExecutor {

    public StreamCommand() {
        super("stream", "yt", "twitch", "youtube");

        setGroup(Group.YOUTUBE);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        CorePlayer player = (CorePlayer) sender;
        StreamManager streamManager = StreamManager.INSTANCE;

        if (args.length == 0) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, пиши §5/stream <ссылка>");
            return;
        }

        if (streamManager.getActiveStream(player) != null) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, у тебя уже есть запущенный стрим!");
            return;
        }

        AbstractStreamDetails streamDetails = null;

        for (StreamPlatform<?> streamPlatform : streamManager.getAvailableStreamPlatforms()) {
            streamDetails = streamPlatform.parseStreamUrl(args[0]);

            if (streamDetails != null) {
                break;
            }
        }

        if (streamDetails == null) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, ссылка на стрим введена неверно!");
            return;
        }

        player.sendMessage("§d§lNeko§f§lCloud §8| §fВаш стрим под ссылкой §e" + args[0] + " §fбыл успешно добавлен!");
        streamManager.addPlayerStream(player, streamDetails);
    }

}
