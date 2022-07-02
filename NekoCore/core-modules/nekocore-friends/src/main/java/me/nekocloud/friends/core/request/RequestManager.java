package me.nekocloud.friends.core.request;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.base.gamer.sections.FriendsSection;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.friends.core.CoreFriend;
import me.nekocloud.friends.core.Friends;
import me.nekocloud.friends.core.event.FriendInviteEvent;
import me.nekocloud.friends.packet.FriendsChangePacket;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RequestManager {

    private final Friends friends;

    public void createRequest(CorePlayer corePlayer, CorePlayer invited) {
        val playerData = getRequestData(corePlayer);
        val inviteData = getRequestData(invited);

        if (playerData.sentInvite(invited.getPlayerID()) && inviteData.hasInvite(corePlayer.getPlayerID())) {
            corePlayer.sendMessageLocale("FRIENDS_ALREADY_SENT_REQUEST");
            return;
        }

        val event = new FriendInviteEvent(invited, corePlayer);
        friends.getCore().callEvent(event);

        if (event.isCancelled()) {
            corePlayer.sendMessageLocale( "FRIENDS_INVITE_EVENT_CANCELLED");
            return;
        }

        addRequest(inviteData, playerData);

        invited.sendMessageLocale("FRIENDS_INVITE", corePlayer.getName());
        invited.sendTitle(
                invited.getLanguage().getMessage("FRIENDS_INVITE_TITLE", corePlayer.getDisplayName()),
                invited.getLanguage().getMessage("FRIENDS_INVITE_SUBTITLE"),
                20,
                30,
                20);

        corePlayer.sendMessageLocale("FRIENDS_INVITE_SEND", invited.getName());

        friends.getCore().getTaskScheduler().schedule(friends, () -> {
            val dataPlayer = getRequestData(corePlayer);
            val dataInvite = getRequestData(invited);

            if (dataPlayer.sentInvite(invited.getPlayerID()) && dataInvite
                    .hasInvite(corePlayer.getPlayerID())) {
                removeRequest(dataInvite, dataPlayer);
                if (corePlayer.isOnline())
                    corePlayer.sendMessageLocale("FRIENDS_INVITE_EXPIRED", invited.getName());
            }
        }, 5L, TimeUnit.MINUTES);
    }

    public void acceptRequest(String name, CorePlayer corePlayer) {
        val invited = friends.getCore().getOfflinePlayer(name);
        if (invited == null)
            return;

        val playerData = getRequestData(corePlayer); //кто принимает
        val inviteData = getRequestData(invited); //кто пригласил

        if (!playerData.hasInvite(inviteData.getId()) || !inviteData
                .sentInvite(playerData.getId())) {
            corePlayer.sendMessageLocale("FRIENDS_DONT_HAVE_INVITES_FROM",
                    invited.getName());
            return;
        }

        removeRequest(playerData, inviteData);

        corePlayer.sendMessageLocale("FRIENDS_NEW_FRIEND", invited.getDisplayName());
        if (invited.isOnline()) {
            invited.sendMessageLocale("FRIENDS_NEW_FRIEND", corePlayer.getDisplayName());
        }

        val invitedFriends = invited.getSection(FriendsSection.class);
        val targetFriends = corePlayer.getSection(FriendsSection.class);

        invitedFriends.changeFriend(FriendAction.ADD_FRIEND, new CoreFriend(corePlayer.getPlayerID()));
        targetFriends.changeFriend(FriendAction.ADD_FRIEND, new CoreFriend(invited.getPlayerID()));

        if (corePlayer.isOnline() && corePlayer.getBukkit() != null)
            corePlayer.getBukkit().sendPacket(new FriendsChangePacket(corePlayer.getPlayerID(), invited.getPlayerID(), FriendAction.ADD_FRIEND));
        if (invited.isOnline() && invited.getBukkit() != null)
            invited.getBukkit().sendPacket(new FriendsChangePacket(invited.getPlayerID(), corePlayer.getPlayerID(), FriendAction.ADD_FRIEND));
    }

    public void rejectRequest(String name, CorePlayer corePlayer) {
        val invited = friends.getCore().getOfflinePlayer(name);
        if (invited == null)
            return;

        val playerData = getRequestData(corePlayer); //кто отклоняет
        val inviteData = getRequestData(invited); //кто пригласил

        if (!playerData.hasInvite(inviteData.getId()) || !inviteData.sentInvite(playerData.getId())) {
            corePlayer.sendMessageLocale("FRIENDS_DONT_HAVE_INVITES_FROM", invited.getName());
            return;
        }

        removeRequest(playerData, inviteData);

        corePlayer.sendMessageLocale( "FRIENDS_YOU_REJECTED_REQUEST", invited.getName());
        if (invited.isOnline()) {
            invited.sendMessageLocale("FRIENDS_REQUEST_REJECTED", corePlayer.getName());
        }
    }

    private void addRequest(Request data, Request other) {
        data.recieveInvite(other.getId());
        other.sendInvite(data.getId());
    }

    public Request getRequestData(CorePlayer invited) {
        if (invited.getData("friend-req-data") == null) {
            invited.addData("friend-req-data", new Request(invited));
        }

        return invited.getData("friend-req-data");
    }

    private void removeRequest(Request data, Request other) {
        data.deleteInvite(other.getId());
        other.cancelInvite(data.getId());
    }
}
