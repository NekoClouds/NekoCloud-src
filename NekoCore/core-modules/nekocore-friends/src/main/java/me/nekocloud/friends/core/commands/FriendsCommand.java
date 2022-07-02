package me.nekocloud.friends.core.commands;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.val;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.base.gamer.sections.FriendsSection;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.friends.core.CoreFriend;
import me.nekocloud.friends.core.Friends;
import me.nekocloud.friends.packet.FriendsChangePacket;
import me.nekocloud.friends.packet.FriendsListPacket;
import me.nekocloud.friends.packet.FriendsRequestListPacket;

public class FriendsCommand extends CommandExecutor {

    private final Friends friends;

    public FriendsCommand(Friends friends) {
        super("friend", "friends", "f");

        setOnlyPlayers(true);
        setCooldown(20, "friends_command");

        this.friends = friends;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            player.sendMessageLocale("COMMAND_FRIENDS_HELP");
            return;
        }

        FriendsSection friendsSection = player.getSection(FriendsSection.class);
        CorePlayer target;
        switch (args[0].toLowerCase()) {
            case "accept" -> {
                if (args.length < 2) {
                    player.sendMessageLocale("COMMAND_FRIENDS_ACCEPT_FORMAT");
                    return;
                }
                if (friendsSection.getFriends().size() >= friendsSection.getFriendsLimit()) {
                    player.sendMessageLocale("FRIENDS_LIMIT", friendsSection.getFriendsLimit());
                    return;
                }
                friends.getRequestManager().acceptRequest(args[1], player);
            }
            case "deny", "decline" -> {
                if (args.length < 2) {
                    player.sendMessageLocale("COMMAND_FRIENDS_DENY_FORMAT");
                    return;
                }
                friends.getRequestManager().rejectRequest(args[1], player);
            }
            case "requests" -> {
                IntSet incoming = friends.getRequestManager().getRequestData(player).getIncoming();
                if (incoming.isEmpty()) {
                    player.sendMessageLocale("FRIENDS_NO_REQUESTS");
                    return;
                }
                if (player.getBukkit() != null)
                    player.getBukkit().sendPacket(new FriendsRequestListPacket(player.getPlayerID(), incoming));
                else
                    player.sendMessageLocale("ERROR_WITH_YOUR_BUKKIT");
            }
            case "list" -> {
                if (friendsSection.getFriends().isEmpty()) {
                    player.sendMessageLocale("FRIENDS_NO_FRIENDS");
                    return;
                }
                if (player.getBukkit() != null) {
                    player.getBukkit().sendPacket(new FriendsListPacket(
                            player.getPlayerID(),
                            friendsSection.getFriends()
                    ));
                } else {
                    player.sendMessageLocale("ERROR_WITH_YOUR_BUKKIT");
                }
            }
            case "add" -> {
                if (args.length < 2) {
                    player.sendMessageLocale("COMMAND_FRIENDS_ADD_FORMAT");
                    return;
                }

                if (getCore().getNetworkManager().getPlayerID(args[0]) == -1) {
                    player.sendMessageLocale("PLAYER_NEVER_PLAYED", args[1]);
                    return;
                }

                target = NekoCore.getInstance().getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    player.sendMessageLocale("PLAYER_NOT_ONLINE", args[1]);
                    return;
                }
                if (friendsSection.getFriends().size() >= friendsSection.getFriendsLimit()) {
                    player.sendMessageLocale("FRIENDS_LIMIT", friendsSection.getFriendsLimit());
                    return;
                }
                if (friendsSection.getFriends().contains(target.getPlayerID())) {
                    player.sendMessageLocale("FRIENDS_ALREADY_FRIEND");
                    return;
                }
                if (player == target) {
                    player.sendMessageLocale("FRIENDS_YOURSELF_AS_FRIEND");
                    return;
                }
                FriendsSection targetFriends = target.getSection(FriendsSection.class);
                if (targetFriends.getFriends().size() >= targetFriends.getFriendsLimit()) {
                    player.sendMessageLocale("FRIENDS_TARGET_LIMIT");
                    return;
                }
                if (((IntList) target.getData("ignore_list")).contains(player.getPlayerID())) {
                    player.sendMessageLocale("FRIENDS_PLAYER_IGNORE");
                    return;
                }
                if (friends.getRequestManager().getRequestData(player).hasInvite(target.getPlayerID())) {
                    friends.getRequestManager().acceptRequest(args[1], player);
                    return;
                }
                friends.getRequestManager().createRequest(player, target);
            }
            case "tp" -> {
                if (args.length < 2) {
                    player.sendMessageLocale("COMMAND_FRIENDS_TP_FORMAT");
                    return;
                }
                target = NekoCore.getInstance().getPlayer(args[1]);
                if (target == null || !target.isOnline() || target.getBukkit() == null) {
                    player.sendMessageLocale("PLAYER_NOT_ONLINE", args[1]);
                    return;
                }
                if (!friendsSection.getFriends().contains(target.getPlayerID())) {
                    player.sendMessageLocale("FRIENDS_NOT_FRIEND");
                    return;
                }
                if (player.getBukkit() == target.getBukkit()) {
                    player.sendMessageLocale("FRIENDS_SERVERS_SAME");
                    return;
                }
                if (target.getBukkit().getName().startsWith("auth")) {
                    player.sendMessageLocale("FRIENDS_DENIED_SERVER");
                    return;
                }
                player.sendMessageLocale("FRIENDS_REDIRECTING", target.getName(), target.getBukkit().getName());
                player.redirect(target.getBukkit());
            }
            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessageLocale("COMMAND_FRIENDS_REMOVE_FORMAT");
                    return;
                }
                if (getCore().getNetworkManager().getPlayerID(args[0]) == -1) {
                    player.sendMessageLocale("PLAYER_NEVER_PLAYED", args[1]);
                    return;
                }
                target = NekoCore.getInstance().getOfflinePlayer(args[1]);
                if (!friendsSection.getFriends().contains(target.getPlayerID())) {
                    player.sendMessageLocale("FRIENDS_NOT_FRIEND");
                    return;
                }
                player.sendMessageLocale("FRIENDS_FRIEND_REMOVED", target.getName());
                if (target.isOnline() && target.getBukkit() != null) {
                    target.sendMessageLocale("FRIENDS_FRIEND_REMOVED_YOU", player.getName());
                    target.getSection(FriendsSection.class).changeFriend(FriendAction.REMOVE_FRIEND, new CoreFriend(player.getPlayerID()));
                    target.getBukkit().sendPacket(new FriendsChangePacket(target.getPlayerID(), player.getPlayerID(), FriendAction.REMOVE_FRIEND));
                }
                friendsSection.changeFriend(FriendAction.REMOVE_FRIEND, new CoreFriend(target.getPlayerID()));
                if (player.getBukkit() != null)
                    player.getBukkit().sendPacket(new FriendsChangePacket(player.getPlayerID(), target.getPlayerID(), FriendAction.REMOVE_FRIEND));
            }
            default -> player.sendMessageLocale("COMMAND_FRIENDS_HELP");
        }
    }
}
