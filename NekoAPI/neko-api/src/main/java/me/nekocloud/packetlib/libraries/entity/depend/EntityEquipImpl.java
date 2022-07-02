package me.nekocloud.packetlib.libraries.entity.depend;

import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.api.entity.EntityEquip;
import me.nekocloud.api.entity.EquipType;
import me.nekocloud.packetlib.nms.NmsAPI;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityEquipImpl implements EntityEquip {

    protected static final PacketContainer PACKET_CONTAINER = NmsAPI.getManager().getPacketContainer();

    protected final Map<EquipType, ItemStack> items = new ConcurrentHashMap<>();

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
    public void removeItems() {
        items.keySet().forEach(this::removeItem);
    }
}
