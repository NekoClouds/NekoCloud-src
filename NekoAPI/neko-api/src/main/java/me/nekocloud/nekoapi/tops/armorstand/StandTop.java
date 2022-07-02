package me.nekocloud.nekoapi.tops.armorstand;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.depend.CraftVector;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.api.hologram.lines.TextHoloLine;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.locale.Language;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class StandTop {

    static HologramAPI HOLOGRAM_API = NekoCloud.getHologramAPI();
    static EntityAPI ENTITY_API = NekoCloud.getEntityAPI();

    int position;
    Top topType;
    CustomStand customStand;
    Map<Language, Hologram> holograms = new ConcurrentHashMap<>();

    Location location;

    @NonFinal StandTopData standTopData;

    public StandTop(final Top topType,
                    final Location location,
                    final int position) {
        this.position = position;
        this.topType = topType;
        this.location = location;

        customStand = ENTITY_API.createStand(location);
        customStand.setArms(true);
        customStand.setSmall(true);
        customStand.setBasePlate(false);

        setEquipment();

        for (val language : Language.values()) {
            val hologram = HOLOGRAM_API.createHologram(location.clone().add(0.0, 1.2, 0.0));

            hologram.addTextLine(language.getMessage("TOP_POSITION", String.valueOf(position)));
            hologram.addTextLine("§cN/A");
            hologram.addTextLine("§cДанные отсутствуют");

            holograms.put(language, hologram);
        }
    }

    public void setStandTopData(final StandTopData standTopData) {
        this.standTopData = standTopData;
        update();
    }

    /**
     * обновить информацию которая сейчас на стендах
     * standTopData - тут хранится голова и кол-во того, что стенд отображает
     */
    public void update() {
        if (standTopData == null)
            return;

        for (val entry : holograms.entrySet()) {
            val hologram = entry.getValue();
            val language = entry.getKey();

            final TextHoloLine holoLine1 = hologram.getHoloLine(1);
            final TextHoloLine holoLine2 = hologram.getHoloLine(2);

            holoLine1.setText(standTopData.getDisplayName());
            holoLine2.setText(standTopData.getLastString(language));
        }

        customStand.getEntityEquip().setHelmet(standTopData.getHead());
    }

    void updateSkinAndPrefix(final BukkitGamer gamer) {
        if (standTopData == null)
            return;

        standTopData.setGamer(gamer);
        update();
    }

    public Location getLocation() {
        return location.clone();
    }

    private void setEquipment() {
        Color color;
        ItemStack itemInHand;
        val v1_12 = SVersionUtil.is1_12();
        switch (position) {
            case 1 -> {
                color = Color.fromRGB(23, 164, 79);
                itemInHand = new ItemStack(Material.EMERALD);
                customStand.setLeftArmPose(new CraftVector(-20, 0, -120));
                customStand.setRightArmPose(new CraftVector(-40, 50, 90));
                customStand.setRightLegPose(new CraftVector(-10, 70, 40));
                customStand.setLeftLegPose(new CraftVector(-10, -60, -40));
                customStand.setHeadPose(new CraftVector(15, 0, 0));
            }
            case 2 -> {
                color = Color.fromRGB(46, 210, 185);
                itemInHand = new ItemStack(Material.DIAMOND);
                customStand.setLeftArmPose(new CraftVector(-20, 0, -140));
                customStand.setRightArmPose(new CraftVector(-50, 20, 10));
                customStand.setRightLegPose(new CraftVector(-5, -10, 10));
                customStand.setLeftLegPose(new CraftVector(-10, -10, -6));
                customStand.setHeadPose(new CraftVector(5, 0, 5));
            }
            case 3 -> {
                color = Color.fromRGB(179, 132, 16);
                itemInHand = new ItemStack(Material.GOLD_INGOT);
                customStand.setLeftArmPose(new CraftVector(50, 15, -7));
                customStand.setRightArmPose(new CraftVector(-50, 10, 5));
                customStand.setRightLegPose(new CraftVector(-20, 0, 5));
                customStand.setLeftLegPose(new CraftVector(20, 0, -5));
                customStand.setHeadPose(new CraftVector(0, 0, 2));
            }
            case 4 -> {
                color = Color.fromRGB(190, 190, 194);
                itemInHand = new ItemStack(Material.IRON_INGOT);
                customStand.setLeftArmPose(new CraftVector(-10, 0, -60));
                customStand.setRightArmPose(new CraftVector(-10, 0, 130));
                customStand.setRightLegPose(new CraftVector(0, 0, 60));
                customStand.setHeadPose(new CraftVector(0, 0, 10));
            }
            case 5 -> {
                color = Color.fromRGB(176, 86, 63);
                if (v1_12) itemInHand = new ItemStack(Material.getMaterial("CLAY_BRICK"));
                else itemInHand = new ItemStack(Material.getMaterial("BRICK"));
                customStand.setLeftArmPose(new CraftVector(-90, -20, -30));
                customStand.setRightArmPose(new CraftVector(50, 30, 90));
                customStand.setRightLegPose(new CraftVector(50, 0, 15));
                customStand.setLeftLegPose(new CraftVector(-7, -6, -5));
                customStand.setHeadPose(new CraftVector(30, 10, 10));
                customStand.setBodyPose(new CraftVector(6, 6, 0));
            }
            default -> {
                color = Color.fromRGB(0, 0, 0);
                itemInHand = new ItemStack(Material.BARRIER);
            }
        }

        val equip = customStand.getEntityEquip();
        equip.setChestplate(ItemUtil.getBuilder(Material.LEATHER_CHESTPLATE)
                .setColor(color)
                .build());
        equip.setLeggings(ItemUtil.getBuilder(Material.LEATHER_LEGGINGS)
                .setColor(color)
                .build());
        equip.setBoots(ItemUtil.getBuilder(Material.LEATHER_BOOTS)
                .setColor(color)
                .build());

        if (v1_12) equip.setHelmet(ItemUtil.getBuilder(Material.getMaterial("SKULL_ITEM"))
                        .setDurability((short) 3)
                        .build());
        else equip.setHelmet(ItemUtil.getBuilder(Material.getMaterial("PLAYER_HEAD")).build());
        equip.setItemInMainHand(itemInHand);

    }

    public void removeTo(final BukkitGamer gamer,
                         final Language language,
                         final boolean hideStand) {
        if (gamer == null)
            return;

        val hologram = holograms.get(language);
        if (hologram != null) hologram.removeTo(gamer);
        else holograms.get(Language.DEFAULT).removeTo(gamer);

        if (hideStand) customStand.removeTo(gamer);
    }

    public void showTo(final BukkitGamer gamer,
                       final Language language,
                       final boolean showStand) {
        if (gamer == null)
            return;

        val hologram = holograms.get(language);
        if (hologram != null) hologram.showTo(gamer);
        else holograms.get(Language.DEFAULT).showTo(gamer);

        if (showStand) customStand.showTo(gamer);
    }
}
