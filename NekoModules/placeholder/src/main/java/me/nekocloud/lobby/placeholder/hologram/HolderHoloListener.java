package me.nekocloud.lobby.placeholder.hologram;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerInteractHologramEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.lobby.placeholder.PlaceHolder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.event.EventPriority.MONITOR;

public class HolderHoloListener extends DListener<PlaceHolder> {

    public HolderHoloListener(PlaceHolder placeHolder) {
        super(placeHolder);
    }

    @EventHandler
    public void onGamerJoin(@NotNull AsyncGamerJoinEvent e) {

    }

    @EventHandler(priority = MONITOR)
    public void onChangeLang(@NotNull GamerChangeLanguageEvent e) {

    }

    @EventHandler
    public void onClickHolo(@NotNull GamerInteractHologramEvent e) {

    }
}
