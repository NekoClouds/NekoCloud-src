package me.nekocloud.lobby.placeholder.hologram;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PROTECTED;

@Getter
@FieldDefaults(level = PROTECTED, makeFinal = true)
public abstract class AbstractHolderHolo {

    HologramAPI hologramAPI = NekoCloud.getHologramAPI();
    Hologram hologram;

    protected AbstractHolderHolo(@NotNull Location loc) {
        hologram = hologramAPI.createHologram(loc.clone());
    }

    public void showTo(@NotNull Player player) {
        hologram.showTo(player);
    }

    public void removeTo(@NotNull Player player) {
        hologram.removeTo(player);
    }

    public void remove() {
        hologram.remove();
    }
}
