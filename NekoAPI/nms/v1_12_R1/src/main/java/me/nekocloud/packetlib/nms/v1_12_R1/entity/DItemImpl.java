package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityItem;
import net.minecraft.server.v1_12_R1.EntityItem;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class DItemImpl extends DEntityBase<EntityItem> implements DEntityItem {

    public DItemImpl(World world) {
        super(new EntityItem(world));
    }

    @Override
    public void setItemStack(ItemStack itemStack) {
        entity.setItemStack(CraftItemStack.asNMSCopy(itemStack));
    }
}
