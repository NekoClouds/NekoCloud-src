package me.nekocloud.core.discord.api;

import com.google.gson.Gson;
import lombok.Getter;
import me.nekocloud.core.NekoCore;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class DiscordAPI {

    public static @Getter final Gson GSON = new Gson();
    public static @Getter final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static void sendPrivateMessage(User user, Message content) {
        user.openPrivateChannel().queue((channel) ->
                channel.sendMessage(content).queue());
    }
}
