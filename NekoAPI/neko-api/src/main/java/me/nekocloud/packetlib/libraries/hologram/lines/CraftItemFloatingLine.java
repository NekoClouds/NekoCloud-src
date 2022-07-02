package me.nekocloud.packetlib.libraries.hologram.lines;

import me.nekocloud.api.depend.CraftVector;
import me.nekocloud.api.entity.EntityEquip;
import me.nekocloud.api.hologram.lines.ItemFloatingLine;
import me.nekocloud.packetlib.libraries.hologram.CraftHologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class CraftItemFloatingLine extends CraftHoloLine implements ItemFloatingLine {

    private ItemStack item;
    private float yaw = 0.0f;
    private boolean rotate;

    public CraftItemFloatingLine(CraftHologram hologram, boolean rotate, Location location, ItemStack item) {
        super(hologram, location.clone());
        customStand.setSmall(false);
        this.rotate = rotate;
        setItem(item);
    }

    @Override
    public boolean isRotate() {
        return rotate;
    }

    @Override
    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    @Override
    public ItemStack getItem(){
        return item.clone();
    }

    @Override
    public void setItem(ItemStack item){
        this.item = item;

        EntityEquip entityEquip = customStand.getEntityEquip();
        if (item.getType().isBlock()){
            entityEquip.setHelmet(item);
            return;
        }

        switch (item.getType()) {
            case BOW, DIAMOND_AXE, IRON_AXE, GOLD_AXE, STONE_AXE, WOOD_AXE, IRON_SWORD, GOLD_SWORD, STONE_SWORD, WOOD_SWORD, DIAMOND_SWORD, IRON_PICKAXE, WOOD_PICKAXE, DIAMOND_PICKAXE, GOLD_PICKAXE, STONE_PICKAXE, IRON_SPADE, STONE_SPADE, DIAMOND_SPADE, WOOD_SPADE, GOLD_SPADE, WOOD_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE, STONE_HOE, STICK, BLAZE_ROD, BONE, FISHING_ROD, CARROT_STICK -> {
                customStand.setRightArmPose(new CraftVector(-135, -90, 0));
                entityEquip.setItemInMainHand(item);
            }
            case SKULL_ITEM, LEATHER_HELMET, GOLD_HELMET, CHAINMAIL_HELMET, IRON_HELMET, DIAMOND_HELMET -> entityEquip.setHelmet(item);
            case LEATHER_CHESTPLATE, GOLD_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE -> entityEquip.setChestplate(item);
            case LEATHER_LEGGINGS, GOLD_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS -> entityEquip.setLeggings(item);
            case LEATHER_BOOTS, GOLD_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, DIAMOND_BOOTS -> entityEquip.setBoots(item);
        }
    }

    public void update(){
        if (!rotate)
            return;

        if (yaw >= 360){
            yaw = 0.0f;
        } else {
            customStand.setLook(yaw, 0.0f);
            yaw += 4.0f;
        }
    }

    public static double getItemFloatingEnter(ItemStack item){
        if (item.getType().isBlock())
            return 0.35;

        double enter = 0;
        switch (item.getType()) {
            case BOW:
                enter = 0.45;
                break;
            case SKULL_ITEM:
            case LEATHER_HELMET:
            case GOLD_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
                enter = 0.35;
                break;
            case LEATHER_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                enter = 0.85;
                break;
            case LEATHER_LEGGINGS:
            case GOLD_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
                enter = 1.4;
                break;
            case LEATHER_BOOTS:
            case GOLD_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                enter = 1.85;
                break;
        }
        return enter;
    }
}
