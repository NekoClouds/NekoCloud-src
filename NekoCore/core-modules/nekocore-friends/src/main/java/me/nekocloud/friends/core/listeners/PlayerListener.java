package me.nekocloud.friends.core.listeners;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.gamer.sections.FriendsSection;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.EventPriority;
import me.nekocloud.core.api.event.player.PlayerAuthCompleteEvent;
import me.nekocloud.core.api.event.player.PlayerLoadSectionEvent;
import me.nekocloud.friends.core.Friends;
import me.nekocloud.friends.core.event.FriendInviteEvent;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class PlayerListener implements EventListener {

    private final Friends friends;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoadSection(PlayerLoadSectionEvent event) {
        event.getSections().add(FriendsSection.class);
    }

    @EventHandler
    public void onJoin(PlayerAuthCompleteEvent event) {
        val player = event.getCorePlayer();
        friends.getCore().getTaskScheduler().runAsync(friends, () -> {
            val onlineFriends = new AtomicInteger();

            player.getSection(FriendsSection.class).getFriends().forEach(friendId -> {
                val friend = friends.getCore().getPlayer(friendId);
                if (friend != null && friend.isOnline()) {
                    friend.sendMessageLocale("FRIEND_JOINED", player.getDisplayName());
                    onlineFriends.getAndIncrement();
                }
            });

            player.sendMessageLocale("FRIENDS_ONLINE", onlineFriends.get());
        });
    }

    @EventHandler
    public void onFriendInvite(FriendInviteEvent event) {
        event.setCancelled(!event.getTarget().getSetting(SettingsType.FRIENDS_REQUEST));
    }
}
