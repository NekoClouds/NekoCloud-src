package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.api.entity.EquipType;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import org.bukkit.inventory.ItemStack;

public interface PacketEntityEquipment extends DPacketEntity<DEntity> {

    void setSlot(EquipType slot);

    EquipType getSlot();

    void setItemStack(ItemStack itemStack);

    ItemStack getItemStack();
}
