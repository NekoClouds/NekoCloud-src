package me.nekocloud.commands.impl;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.utils.redirect.RedirectUtil;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class HubCommand extends CommandExecutor {

    public HubCommand() {
        super("hub", "lobby", "лобби", "дщиин", "хаб", "loby");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCooldown(3, "hub_cmd");
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        val player = (CorePlayer) sender;

        if (player.getBukkit().getName().startsWith("hub")) {
            player.sendMessageLocale("ALREADY_CONNECTED", player.getBukkit().getName());
            return;
        }

        player.redirect(RedirectUtil.getRandomHub());
//        if (args.length == 0) {
//            connect(player, "hub");
//        } else {
//            connect(player, args[0]);
//        }
    }

    private void connect(CorePlayer corePlayer, String serverPrefix) {
        if (NekoCore.getInstance().getServersByPrefix("hub").isEmpty()) {
            corePlayer.sendMessageLocale("NO_HUB_FOUND");
            return;
        }

//        ServerMode serverMode;
//        if (NekoCore.getInstance().getServersByPrefix(serverPrefix).isEmpty()) {
//            corePlayer.sendMessageLocale("NO_HUB_FOUND");
//            return;
//        }
//
//        if ((serverMode = ServerMode.getMode(serverPrefix)).equals(ServerMode.HUB)) {
//            corePlayer.connectToAnyServer("hub");
//            return;
//        }
//
//        if (serverMode.getSubModes(ServerSubModeType.GAME_LOBBY).isEmpty()) {
//            corePlayer.sendMessageLocale("NO_LOBBY_FOUND");
//        } else {
//            val lobbyServer = NekoCore.getInstance().getBestServer(
//                    serverMode.getSubModes(ServerSubModeType.GAME_LOBBY)
//                            .stream()
//                            .findFirst()
//                            .getById());
//            if (lobbyServer != null) corePlayer.connectToServer(lobbyServer);
//            else corePlayer.sendMessageLocale("AVAILABLE_SERVER_NOT_FOUND");
//        }
    }
}
