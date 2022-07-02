package me.nekocloud.packetlib.libraries.hologram.lines;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.hologram.HoloLine;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.packetlib.libraries.hologram.CraftHologram;
import me.nekocloud.packetlib.libraries.hologram.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class CraftHoloLine implements HoloLine {

    private static final EntityAPI ENTITY_API = NekoCloud.getEntityAPI();

    private final HologramManager hologramManager;
    protected final Hologram hologram;
    protected final CustomStand customStand;

    CraftHoloLine(CraftHologram hologram, Location location) {
        this.hologram = hologram;
        this.hologramManager = hologram.getHologramManager();

        customStand = ENTITY_API.createStand(location);
        customStand.setInvisible(true);
        customStand.setSmall(true);
        customStand.setBasePlate(false);

        hologramManager.addCustomStand(hologram, customStand);
    }

    public void remove() {
        hologramManager.removeCustomStand(customStand);
        customStand.remove();
    }

    @Override
    public CustomStand getCustomStand() {
        return customStand;
    }

    @Override
    public void delete() {
        hologram.removeLine(this);
    }

    public void teleport(Location location){
        customStand.onTeleport(location);
    }

    public void showTo(Player player) {
        customStand.showTo(player);
    }

    public void hideTo(Player player) {
        customStand.removeTo(player);
    }

    public void setPublic(boolean vision) {
        customStand.setPublic(vision);
    }

    public boolean isVisibleTo(Player player) {
        return customStand.isVisibleTo(player);
    }

}
