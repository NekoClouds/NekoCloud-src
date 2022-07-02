package me.nekocloud.nekoapi.tops.armorstand;

import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerChangePrefixEvent;
import me.nekocloud.api.event.gamer.GamerChangeSkinEvent;
import me.nekocloud.api.event.gamer.GamerInteractHologramEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class StandTopListener extends DListener<JavaPlugin> {

    private final TopManager manager;

    StandTopListener(@NotNull TopManager standTopManager) {
        super(standTopManager.getJavaPlugin());

        this.manager = standTopManager;
    }

    @EventHandler
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        if (manager.size() < 1) {
            return;
        }

        manager.getStandPlayerStorage().addPlayer(new StandPlayer(manager, e.getGamer()));
    }

    @EventHandler
    public void onRemove(final @NotNull AsyncGamerQuitEvent e) {
        manager.getStandPlayerStorage().removePlayer(e.getGamer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(final @NotNull GamerInteractHologramEvent e) {
        final Hologram hologram = e.getHologram();
        final BukkitGamer gamer = e.getGamer();

        if (manager.size() <= 1) {
            return;
        }

        final StandPlayer standPlayer = manager.getStandPlayerStorage().getPlayer(gamer);
        if (standPlayer == null) {
            return;
        }

        final Top topType = standPlayer.getTopType();
        final Hologram mainHologram = topType.getHologramMiddle(gamer.getLanguage());
        if (mainHologram == null || mainHologram != hologram) {
            return;
        }

        standPlayer.changeSelected();
        gamer.playSound(SoundType.CLICK);
    }

    @EventHandler
    public void onChangeLang(final @NotNull GamerChangeLanguageEvent e) {
        final Language old = e.getOldLanguage();
        final Language lang = e.getLanguage();
        final BukkitGamer gamer = e.getGamer();

        final StandPlayer standPlayer = manager.getStandPlayerStorage().getPlayer(gamer);
        if (standPlayer == null) {
            return;
        }

        standPlayer.changeHolo(old, lang);

        gamer.playSound(SoundType.CLICK);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeSkin(final @NotNull GamerChangeSkinEvent e) {
        changeStands(e.getGamer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangePrefix(final @NotNull GamerChangePrefixEvent e) {
        changeStands(e.getGamer());
    }

    private void changeStands(BukkitGamer gamer) {
        BukkitUtil.runTaskAsync(() -> manager.getAllStands().stream()
                .filter(standTop -> standTop.getStandTopData() != null)
                .filter(standTop -> standTop.getStandTopData().getPlayerID() == gamer.getPlayerID())
                .forEach(standTop -> standTop.updateSkinAndPrefix(gamer)));
    }

}
