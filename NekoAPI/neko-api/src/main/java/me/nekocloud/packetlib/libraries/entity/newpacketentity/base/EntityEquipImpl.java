package me.nekocloud.packetlib.libraries.entity.newpacketentity.base;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.RequiredArgsConstructor;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerEntityEquipment;
import me.nekocloud.api.entity.EntityEquip;
import me.nekocloud.api.entity.EquipType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class EntityEquipImpl implements EntityEquip {

    private final Map<EquipType, ItemStack> items = new ConcurrentHashMap<>();
    private final DPacketEntityBase entity;

    @Nullable
    @Override
    public ItemStack getHelmet() {
        return getItem(EquipType.HEAD);
    }

    @Nullable
    @Override
    public ItemStack getChestplate() {
        return getItem(EquipType.CHEST);
    }

    @Nullable
    @Override
    public ItemStack getLeggings() {
        return getItem(EquipType.LEGS);
    }

    @Nullable
    @Override
    public ItemStack getBoots() {
        return getItem(EquipType.FEET);
    }

    @Nullable
    @Override
    public ItemStack getItemInMainHand() {
        return getItem(EquipType.MAINHAND);
    }

    @Nullable
    @Override
    public ItemStack getItemInOffHand() {
        return getItem(EquipType.OFFHAND);
    }

    @Nullable
    @Override
    public ItemStack getItem(EquipType equipType) {
        ItemStack item = items.get(equipType);
        if (item != null)
            return item.clone();

        return null;
    }

    @Override
    public void setItem(EquipType equipType, ItemStack itemStack) {
        ItemStack old = items.get(equipType);
        if (old != null && old.equals(itemStack))
            return;

        items.put(equipType, itemStack);

        entity.sendPacket(getPacket(equipType, itemStack));
    }

    @Override
    public Map<EquipType, ItemStack> getItemsEquip() {
        return new HashMap<>(items);
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        setItem(EquipType.HEAD, helmet);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        setItem(EquipType.CHEST, chestplate);
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        setItem(EquipType.LEGS, leggings);
    }

    @Override
    public void setBoots(ItemStack boots) {
        setItem(EquipType.FEET, boots);
    }

    @Override
    public void setItemInMainHand(ItemStack item) {
        setItem(EquipType.MAINHAND, item);
    }

    @Override
    public void setItemInOffHand(ItemStack item) {
        setItem(EquipType.OFFHAND, item);
    }

    @Override
    public boolean removeItem(EquipType equipType) {
        if (items.remove(equipType) != null) {

            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
            packet.setEntityID(entity.getEntityID());
            packet.setSlot(EnumWrappers.ItemSlot.valueOf(equipType.name()));
            packet.setItem(null);
            entity.sendPacket(packet);

            return true;
        }

        return false;
    }

    @Override
    public void removeItems() {
        items.keySet().forEach(this::removeItem);
    }

    private WrapperPlayServerEntityEquipment getPacket(EquipType equipType, ItemStack itemStack) {
        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
        packet.setEntityID(entity.getEntityID());
        packet.setSlot(EnumWrappers.ItemSlot.valueOf(equipType.name()));
        packet.setItem(itemStack);

        return packet;
    }

    public void sendAllItems(Player player) {
        items.forEach((equipType, itemStack) -> getPacket(equipType, itemStack).sendPacket(player));
    }
}
