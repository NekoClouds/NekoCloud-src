package me.nekocloud.lobby.profile.hider;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerFriendEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class HiderListener extends DListener<Lobby> {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    public HiderListener(Lobby lobby) {
        super(lobby);
        Arrays.stream(Language.values()).forEach(HiderItem::new);
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();
        val player = gamer.getPlayer();

        if (player == null || !player.isOnline()) {
            return;
        }

        HiderItem.giveToPlayer(player, gamer.getLanguage(), gamer.getSetting(SettingsType.HIDER));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        val player = e.getPlayer();
        val gamer = gamerManager.getGamer(player);
        if (gamer == null) {
            return;
        }

        for (val otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) {
                continue;
            }

            val otherGamer = gamerManager.getGamer(otherPlayer);
            if (otherGamer == null || gamer.getFriends().containsKey(otherGamer.getPlayerID())) {
                continue;
            }

            if (gamer.getSetting(SettingsType.HIDER) && !otherGamer.isJunior()) {
                player.hidePlayer(otherPlayer);
            }

            if (otherGamer.getSetting(SettingsType.HIDER) && !gamer.isJunior()) {
                otherPlayer.hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void onFriend(GamerFriendEvent e) {
        val gamer = e.getGamer();

        val otherGamer = gamerManager.getGamer(e.getFriend().getPlayerId());
        if (otherGamer == null) {
            return;
        }

        val otherPlayer = otherGamer.getPlayer();
        val player = gamer.getPlayer();
        if (player == null || otherPlayer == null || !otherPlayer.isOnline() || !player.isOnline()) {
            return;
        }

        if (e.getAction() == FriendAction.ADD_FRIEND && gamer.getSetting(SettingsType.HIDER)) {
            player.showPlayer(otherPlayer);
        }

        if (e.getAction() == FriendAction.REMOVE_FRIEND
                && gamer.getSetting(SettingsType.HIDER)
                && !otherGamer.isJunior()) {
            player.hidePlayer(otherPlayer);
        }

    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        val gamer = e.getGamer();
        val player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        val lang = e.getLanguage();

        HiderItem.giveToPlayer(player, lang, gamer.getSetting(SettingsType.HIDER));
    }
}
