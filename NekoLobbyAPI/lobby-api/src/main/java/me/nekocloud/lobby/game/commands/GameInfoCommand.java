package me.nekocloud.lobby.game.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.game.GameState;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.lobby.config.GameConfig;
import me.nekocloud.lobby.game.data.Channel;
import me.nekocloud.lobby.game.data.Server;

import java.util.Map;

public class GameInfoCommand implements CommandInterface {

    private final GameConfig gameConfig;

    public GameInfoCommand(GameConfig gameConfig){
        val spigotCommand = COMMANDS_API.register("gameinfo", this, "gi");
        spigotCommand.setGroup(Group.ADMIN);

        this.gameConfig = gameConfig;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        if (args.length > 0) {
            String nameChannel = args[0];
            Channel channel = gameConfig.getChannels().get(nameChannel.toLowerCase());
            if (channel == null) {
                gamerEntity.sendMessageLocale( "CHANNEL_NOT_FOUND", nameChannel);
                return;
            }

            send(channel, gamerEntity);
            return;
        }

        for (Channel channel : gameConfig.getChannels().values())
            send(channel, gamerEntity);
    }

    private static void send(Channel channel, GamerEntity gamerEntity) {
        Map<String, Server> servers = channel.getServers();
        int free = 0;
        int busy = 0;
        int online = channel.getOnline();

        StringBuilder onRestart = new StringBuilder();
        for (Server server : servers.values()) {
            if (!server.isAlive()) {
                if (onRestart.toString().isEmpty()) {
                    onRestart.append(server.getName());
                } else {
                    onRestart.append(", ").append(server.getName());
                }
                continue;
            }

            online += server.getOnline();
            if (server.getGameState() == GameState.WAITING) {
                free++;
            } else {
                busy++;
            }

        }

        gamerEntity.sendMessage(" ");
        gamerEntity.sendMessage("§dКанал §c" + channel.getNameChannel() + "§6:");
        gamerEntity.sendMessage(" §7▪ §dВсего серверов - §7" + servers.size());
        gamerEntity.sendMessage(" §7▪ §dСвободных - §a" + free);
        gamerEntity.sendMessage(" §7▪ §dИдет игра - §e" + busy);
        gamerEntity.sendMessage(" §7▪ §dНа перезагрузке - " + (onRestart.toString().isEmpty() ? "§aВсе арены работают" : "§c" + onRestart));
        gamerEntity.sendMessage(" §7▪ §dОбщий онлайн §e" + online);
        gamerEntity.sendMessage(" ");
    }
}
