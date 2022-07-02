package me.nekocloud.packetlib.nms.interfaces.entity;

import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;

public interface DEntityPlayer extends DEntityLiving {

    String getName();

    GameProfile getProfile();

    @Deprecated //на 1.12 не требуется
    ItemStack getItemInHand();

    int getPing();
}
