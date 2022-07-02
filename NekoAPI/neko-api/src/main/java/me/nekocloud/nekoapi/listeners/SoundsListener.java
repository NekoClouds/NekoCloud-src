package me.nekocloud.nekoapi.listeners;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SoundsListener extends DListener<NekoAPI> {

    final SoundAPI soundAPI = NekoCloud.getSoundAPI();

    @Setter @Getter boolean enable = true; // Это если на каком то режиме надо оффнуть
    @Setter @Getter boolean enableClick = false; // Включаем на мг!!

    public SoundsListener(final NekoAPI nekoAPI) {
        super(nekoAPI);
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        if (!isEnableClick() || event.getCurrentItem() == null || event.getClickedInventory() == null)
            return;

        if (event.getClickedInventory().getLocation() != null && !event.getClickedInventory().getLocation().getBlock().isEmpty())
            return;


        val gamer = GAMER_MANAGER.getGamer(event.getWhoClicked().getName());
        if (gamer == null || !gamer.getSetting(SettingsType.GUI_SOUNDS))
            return;

        if (!(event.getClickedInventory() instanceof Player) && event.getCurrentItem().getType().isItem()) {
            soundAPI.play(gamer.getPlayer(), SoundType.CLICK);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isEnable())
            return;

        if (event.getInventory().getLocation() != null && !event.getInventory().getLocation().getBlock().isEmpty())
            return;


        val gamer = GAMER_MANAGER.getGamer(event.getPlayer().getName());
        if (gamer == null || !gamer.getSetting(SettingsType.GUI_SOUNDS))
            return;

        soundAPI.play(gamer.getPlayer(), SoundType.CLICK);
    }

    @EventHandler
    public void onPlayerItemHeld(final @NotNull PlayerItemHeldEvent event) {
        if (!isEnable())
            return;

        val gamer = GAMER_MANAGER.getGamer(event.getPlayer().getName());
        if (gamer == null || !gamer.getSetting(SettingsType.GUI_SOUNDS))
            return;

        soundAPI.play(gamer.getPlayer(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.3f, 2);
    }

}
