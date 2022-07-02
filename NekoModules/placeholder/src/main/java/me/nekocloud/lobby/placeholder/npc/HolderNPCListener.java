package me.nekocloud.lobby.placeholder.npc;

import lombok.val;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bukkit.BukkitConnector;
import me.nekocloud.lobby.placeholder.PlaceHolder;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class HolderNPCListener extends DListener<PlaceHolder> {

    private final BukkitConnector connector = (BukkitConnector) CoreConnector.getInstance();

    public HolderNPCListener(PlaceHolder placeHolder) {
        super(placeHolder);
    }

    @EventHandler
    public void onClickNPC(@NotNull GamerInteractNPCEvent e) {

        player.sendMessage("§dМяу! Ошибка, я ничего не умею :(");
    }

    @EventHandler
    public void onGamerJoin(@NotNull AsyncGamerJoinEvent e) {

    }

    @EventHandler
    public void onChangeLang(@NotNull GamerChangeLanguageEvent e) {

    }
    
}
