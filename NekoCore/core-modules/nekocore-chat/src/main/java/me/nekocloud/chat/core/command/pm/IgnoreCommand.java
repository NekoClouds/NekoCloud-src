package me.nekocloud.chat.core.command.pm;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.val;
import me.nekocloud.chat.core.manager.IgnoreManager;
import me.nekocloud.chat.packet.IgnoreListPacket;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class IgnoreCommand extends CommandExecutor {

    private final IgnoreManager ignoreManager;

    public IgnoreCommand(IgnoreManager ignoreManager) {
        super("ignore", "игнор", "игнорировать");
        this.ignoreManager = ignoreManager;

        setOnlyPlayers(true);
    }

    @Override // TODO: переписать этот ебаный калл
    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            notEnoughArguments(sender, "CHAT_PREFIX", "IGNORE_FORMAT");
            return;
        }

        val lowerCommand = args[0].toLowerCase();
        if (args.length >= 2 && lowerCommand.equals("add") || lowerCommand.equals("remove")) {
            String ignoredName = args[1];
            if (ignoredName.equalsIgnoreCase(player.getName())) {
                player.sendMessageLocale("IGNORE_PLAYERS_SAME");
                return;
            }
            if (!getCore().getNetworkManager().hasIdentifier(ignoredName)) {
                playerNeverPlayed(sender, ignoredName);
                return;
            }
        }

        CorePlayer ignoredPlayer;
        IntList ignoredList = player.getData("ignore_list");

        switch (lowerCommand) {
            case "add" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "", "");
                    return;
                }

                ignoredPlayer = NekoCore.getInstance().getOfflinePlayer(args[1]);
                if (ignoredList.contains(ignoredPlayer.getPlayerID())) {
                    player.sendMessageLocale("ALREADY_IGNORED", ignoredPlayer.getName());
                    return;
                }

                player.sendMessageLocale("IGNORE_ADD_PLAYER", ignoredPlayer.getName());
                ignoredList.add(ignoredPlayer.getPlayerID());

                ignoreManager.addIgnore(player.getPlayerID(), ignoredPlayer.getPlayerID());
            }

            case "remove" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "", "");
                    return;
                }

                ignoredPlayer = NekoCore.getInstance().getOfflinePlayer(args[1]);
                if (!ignoredList.contains(ignoredPlayer.getPlayerID())) {
                    player.sendMessageLocale("PLAYER_NOT_IGNORED", ignoredPlayer.getName());
                    return;
                }

                player.sendMessageLocale("IGNORE_REMOVE_PLAYER", ignoredPlayer.getName());
                ignoredList.remove(ignoredPlayer.getPlayerID());

                ignoreManager.removeIgnore(player.getPlayerID(), ignoredPlayer.getPlayerID());
            }

            case "list" -> {
                if (ignoredList.size() == 0) {
                    player.sendMessageLocale("IGNORE_LIST_EMPTY");
                    return;
                }

                if (player.getBukkit() == null) {
                    player.sendMessageLocale("ERROR_WITH_YOUR_BUKKIT");
                    return;
                }

                player.getBukkit().sendPacket(new IgnoreListPacket(player.getPlayerID(), ignoredList));
            }

            default -> player.sendMessageLocale("COMMAND_IGNORE_HELP");
        }
    }

}
