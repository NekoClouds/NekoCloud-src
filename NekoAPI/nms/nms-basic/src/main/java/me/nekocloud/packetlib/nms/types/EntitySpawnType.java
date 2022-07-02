package me.nekocloud.packetlib.nms.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntitySpawnType {
    BOAT(1),
    ITEM_STACK(2),
    MINECART(10),
    MINECART_STORAGE(11),
    MINECART_POWERED(12),
    ACTIVATED_TNT(50),
    ENDER_CRYSTAL(51),
    ARROW_PROJECTILE(60),
    SNOWBALL_PROJECTILE(61),
    EGG_PROJECTILE(62),
    FIRE_BALL_GHAST(63),
    FIRE_BALL_BLAZE(64),
    THROWN_ENDERPEARL(65),
    WITHER_SKULL(66),
    FALLING_BLOCK(70),
    ITEM_FRAME(71),
    EYE_OF_ENDER(72),
    THROWN_POTION(73),
    FALLING_DRAGON_EGG(74),
    THROWN_EXP_BOTTLE(75),
    FIREWORK(76),
    ARMOR_STAND(78),
    FISHING_FLOAT(90)
    ;

    private final int id;
}
