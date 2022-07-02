package me.nekocloud.grief.listener;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.grief.Grief;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.event.EventHandler;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerListener extends DListener<Grief> {

    public PlayerListener(Grief grief) {
        super(grief);

    }

    @EventHandler
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        val player = e.getGamer().getPlayer();
        val gamer = e.getGamer();
        val lang = gamer.getLanguage();

    }


}
