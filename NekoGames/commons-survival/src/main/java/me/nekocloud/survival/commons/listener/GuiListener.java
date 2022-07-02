package me.nekocloud.survival.commons.listener;

import lombok.val;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.*;
import me.nekocloud.survival.commons.gui.time.PTimeGui;
import me.nekocloud.survival.commons.gui.time.TimeGui;
import me.nekocloud.survival.commons.gui.weather.PWeatherGui;
import me.nekocloud.survival.commons.gui.weather.WeatherGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuiListener extends DListener<CommonsSurvival> {

    private final GuiManager<CommonsSurvivalGui> manager = CommonsSurvivalAPI.getGuiManager();

    public GuiListener(CommonsSurvival main) {
        super(main);
        val configData = CommonsSurvival.getConfigData();

        //создаем все ГУИ
        if (configData.isKitSystem())
            manager.createGui(KitGui.class);

        if (configData.isHomeSystem())
            manager.createGui(HomeGui.class);

        if (configData.isWarpSystem()) {
            manager.createGui(WarpGui.class);
            manager.createGui(MyWarpGui.class);
        }

        manager.createGui(MainGui.class);
        manager.createGui(PTimeGui.class);
        manager.createGui(TimeGui.class);

        manager.createGui(WeatherGui.class);
        manager.createGui(PWeatherGui.class);

        manager.createGui(TpacceptGui.class);

        if (configData.isRtpSystem())
            manager.createGui(RtpGui.class);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        val player = e.getPlayer();

        manager.removeALL(player);
    }

    @EventHandler
    public void onPlayerOpenGui(PlayerCommandPreprocessEvent e) {
        val player = e.getPlayer();
        if (!e.getMessage().equalsIgnoreCase("/menu"))
            return;

        e.setCancelled(true);
        manager.getGui(MainGui.class, player).open();
    }
}
