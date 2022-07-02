package me.nekocloud.packetlib.libraries.hologram.lines;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerPickupHoloEvent;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.lines.ItemDropLine;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.libraries.hologram.CraftHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class CraftItemDropLine extends CraftHoloLine implements ItemDropLine {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private ItemStack itemStack;
    @Setter
    private boolean pickup;

    public CraftItemDropLine(CraftHologram hologram, Location location, boolean pickup, ItemStack item) {
        super(hologram, location);
        this.pickup = pickup;
        this.itemStack = item;
        this.setItem(itemStack);
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Override
    public void setItem(ItemStack item) {
        customStand.setItemPassenger(item);
    }

    public void checkPickup(Player player, Hologram hologram) {
        if (!(hologram.isVisibleTo(player) || hologram.isPublic())) {
            return;
        }

        double range = LocationUtil.distance(player.getLocation(), customStand.getLocation());
        if (range < 1.5 && range != -1) {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null) {
                return;
            }

            GamerPickupHoloEvent event = new GamerPickupHoloEvent(gamer, hologram, this);
            BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
            if (event.isRemove()) {
                delete();
            }
        }
    }
}
